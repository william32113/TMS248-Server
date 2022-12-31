package net.swordie.ms.client.jobs.flora;

import net.swordie.ms.client.Client;
import net.swordie.ms.client.character.Char;
import net.swordie.ms.client.character.CharacterStat;
import net.swordie.ms.client.character.info.HitInfo;
import net.swordie.ms.client.character.items.Item;
import net.swordie.ms.client.character.skills.Option;
import net.swordie.ms.client.character.skills.SecondAtom;
import net.swordie.ms.client.character.skills.Skill;
import net.swordie.ms.client.character.skills.SkillStat;
import net.swordie.ms.client.character.skills.info.*;
import net.swordie.ms.client.character.skills.temp.CharacterTemporaryStat;
import net.swordie.ms.client.character.skills.temp.TemporaryStatBase;
import net.swordie.ms.client.character.skills.temp.TemporaryStatManager;
import net.swordie.ms.client.jobs.Job;
import net.swordie.ms.connection.InPacket;
import net.swordie.ms.connection.packet.*;
import net.swordie.ms.constants.JobConstants;
import net.swordie.ms.enums.*;
import net.swordie.ms.handlers.EventManager;
import net.swordie.ms.life.*;
import net.swordie.ms.life.mob.Mob;
import net.swordie.ms.life.mob.MobStat;
import net.swordie.ms.life.mob.MobTemporaryStat;
import net.swordie.ms.loaders.ItemData;
import net.swordie.ms.loaders.SkillData;
import net.swordie.ms.util.Position;
import net.swordie.ms.util.Randomizer;
import net.swordie.ms.util.Rect;
import net.swordie.ms.util.Util;
import net.swordie.ms.util.container.Tuple;
import net.swordie.ms.world.field.Field;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static net.swordie.ms.client.character.skills.SkillStat.*;
import static net.swordie.ms.client.character.skills.temp.CharacterTemporaryStat.*;

/**
 * @author Sjonnie
 * Created on 6/25/2018.
 */
public class Adele extends Job {
    //0轉
    public static final int 再訪 = 150021000;
    public static final int 魔法迴路 = 150020079;
    public static final int 信念 = 150020006;//完成某任務會給

    //1轉
    public static final int 碎片 = 151001001;
    public static final int 懸浮 = 151001004;

    //2轉
    public static final int 乙太 = 151100017;
    public static final int 刺擊 = 151101000;
    public static final int 穿刺 = 151101001;
    public static final int 乙太結晶 = 151100002;
    public static final int 魔劍共鳴 = 151101003;
    public static final int 魔劍共鳴_1 = 151101004;
    public static final int 魔劍共鳴_2 = 151101010;
    public static final int 推進器 = 151101005;
    public static final int 創造 = 151101006;
    public static final int 創造_1 = 151101007;
    public static final int 創造_2 = 151101008;
    public static final int 創造_3 = 151101009;



    public static final int 奇蹟 = 151101013;


    //3轉
    public static final int 十字斬 = 151111000;
    public static final int 劍域 = 151111001;
    public static final int 回歸 = 151111002;
    public static final int 追蹤 = 151111003;
    public static final int 羽翼 = 151111004;
    public static final int 高潔精神 = 151111005;

    //4轉
    public static final int 高級乙太 = 151120012;
    public static final int 切割 = 151121000;
    public static final int 死亡標記 = 151121001;
    public static final int 踐踏 = 151121002;
    public static final int 綻放 = 151121003;
    public static final int 護堤 = 151121004;
    public static final int 雷普的勇士 = 151121005;
    public static final int 雷普勇士的意志 = 151121006;

    // Hyper Skills
    public static final int 狂風 = 151121040;
    public static final int 魔力爆裂 = 151121041;
    public static final int 神之種族 = 151121042;
    public static final int 魔劍共鳴_額外治癒 = 151120034;

    // V skills
    public static final int 無限 = 400011108;
    public static final int 復原 = 400011109;

    private int capeCounter = 0;

    private Map<Integer, Integer> capeAtoms = new HashMap<>();
    private List<Summon> summonList = new ArrayList<>();

    private ScheduledFuture aetherTimer;


    private boolean isTriggerSkill(int skillid){
        switch(skillid) {
            case 刺擊:
            case 十字斬:
            case 切割:
            case 踐踏:
                return true;
            default:
                return false;
        }
    }

    public Map<Integer, Integer> getCapeAtoms() {
        return capeAtoms;
    }

    public void setCapeAtoms(Map<Integer, Integer> capeAtoms) {
        this.capeAtoms = capeAtoms;
    }

    public void clearCapeAtoms() {
        getCapeAtoms().clear();
        setCapeCounter(0);
    }

    public void clearAetherTimer() {
        if (aetherTimer != null) {
            aetherTimer.cancel(true);
        }
    }

    public void setCapeCounter(int forceAtomKeyCounter) {
        this.capeCounter = forceAtomKeyCounter;
    }

    public int getNewCapeCounter() {
        return capeCounter++;
    }

    private int[] addedSkills = new int[]{
            再訪,魔法迴路,懸浮
    };

    public Adele(Char chr) {
        super(chr);
        if (chr != null && chr.getId() != 0 && isHandlerOfJob(chr.getJob())) {
            TemporaryStatManager tsm = chr.getTemporaryStatManager();
            for (int id : addedSkills) {
                if (!chr.hasSkill(id)) {
                    Skill skill = SkillData.getSkillDeepCopyById(id);
                    skill.setCurrentLevel(skill.getMasterLevel());
                    chr.addSkill(skill);
                }
            }
            if (aetherTimer != null && !aetherTimer.isDone()) {
                aetherTimer.cancel(true);
            }
            if (chr.hasSkill(乙太)){
                aetherTimer = EventManager.addFixedRateEvent(this::自動獲得乙太, 1000, 3000);//好像是接受到958後自動發不用寫這個
            }

        }
    }

    @Override
    public boolean isHandlerOfJob(short id) {
        return JobConstants.isAdele(id);
    }

    private void 自動獲得乙太() {
        if (chr == null || chr.getField() == null) {
            return;
        }
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        //int delta = tsm.hasStat(CharacterTemporaryStat.高潔精神) ? 40 : 15;
        int currentEnergy = tsm.getOption(CharacterTemporaryStat.乙太).nOption;
        updateAether(currentEnergy);
    }

    private int getCurrentAether() {
        if (chr == null || chr.getField() == null) {
            return 0;
        }
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        int currentEnergy = tsm.getOption(CharacterTemporaryStat.乙太).nOption;
        return currentEnergy;
    }

    private void modifyAether(int change) {
        if (chr == null || chr.getField() == null) {
            return;
        }
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        if (!tsm.hasStat(CharacterTemporaryStat.創造)) {
            return;
        }
        int currentEnergy = tsm.getOption(CharacterTemporaryStat.乙太).nOption;
        updateAether(currentEnergy + change);
    }

    private void updateAether(int aether) {
        if (chr == null) {
            return;
        }
        int maxAether = 300;
        if (chr.hasSkill(高級乙太)) {
            maxAether = 400;
        }
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        o.nOption = (aether > maxAether ? maxAether : (aether < 0 ? 0 : aether));
        o.nReason = 15002;
        o.tOption = 0;
        tsm.putCharacterStatValue(CharacterTemporaryStat.乙太, o);
        tsm.sendSetStatPacket();
    }

    @Override
    public void handleAttack(Client c, AttackInfo attackInfo) {
        List<SecondAtom> seconAtoms = new LinkedList();
        Char chr = c.getChr();
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Skill skill = chr.getSkill(attackInfo.skillId);
        int skillID = 0;
        SkillInfo si = null;
        Summon summon = chr.getField().getSummonByChrAndSkillId(chr, 乙太結晶);
        boolean hasHitMobs = attackInfo.mobAttackInfo.size() > 0;
        int slv = 0;
        if (skill != null) {
            si = SkillData.getSkillInfoById(skill.getSkillId());
            slv = skill.getCurrentLevel();
            skillID = skill.getSkillId();
        }
        Option o = new Option();
        Option o1 = new Option();
        Field field = chr.getField();

        if (hasHitMobs && isTriggerSkill(attackInfo.skillId)) {
            modifyAether(+10);
            if (!chr.hasSkillOnCooldown(創造_1) && tsm.hasStat(CharacterTemporaryStat.創造)) { //if (tsm.hasStat(CharacterTemporaryStat.MassDestructionRockets)) {
                getCapeAtoms().forEach((key, objectID) -> {
                    if (getCurrentAether() >= 100) {
                        chr.getField().broadcastPacket(SecondAtomPacket.secondAtomAttack(chr, objectID, key > 1 ? 0 : getCurrentAether() >= 300 ? 3 : getCurrentAether() >= 200 ? 2 : 1));
                    }
                });
                chr.addSkillCoolTime(創造_1, chr.getJob() == 15112 ? 6000 : chr.getJob() == 15111 ? 9000 : 12000);
            }
            if (!chr.hasSkillOnCooldown(奇蹟) && tsm.hasStat(CharacterTemporaryStat.復原)) { //if (tsm.hasStat(CharacterTemporaryStat.MassDestructionRockets)) {
                Rect rect = chr.getRectAround(new Rect(-600, -600, 600, 600));
                if (!chr.isLeft()) {
                    rect = rect.horizontalFlipAround(chr.getPosition().getX());
                }
                for (int i = 0; i < 5; i++) {
                    List<Mob> lifes = chr.getField().getMobsInRect(rect);
                    if (lifes.size() <= 0) {
                        return;
                    }
                    Mob mob = Util.getRandomFromCollection(chr.getField().getMobsInRect(rect));


                    final SecondAtom fa = new SecondAtom(
                            chr.getField().getNewObjectID(),
                            chr.getId(),
                            mob.getObjectId(),
                            0,
                            碎片,
                            chr.getSkillLevel(碎片),
                            chr.getPosition(),
                            SecondAtomEnum.MagicShard.getVal(),
                            chr.getNewSecondAtomKey(),
                            0,
                            1,
                            i < 1 ? 0 : i < 3 ? 120 : 240,
                            1200,
                            3000,
                            false,
                            null);
                    seconAtoms.add(fa);
                }
                chr.createSecondAtom(seconAtoms, true);
                seconAtoms.clear();
                chr.addSkillCoolTime(奇蹟, 8000);
            }
        }

        switch (attackInfo.skillId) {
            case 死亡標記:
                if (hasHitMobs) {
                    for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        if (mob == null) {
                            continue;
                        }
                        TemporaryStatBase tsb = tsm.getTSBByTSIndex(TSIndex.SecondAtomLockOn);
                        tsb.setNOption(1);
                        tsb.setROption(skillID);
                        tsb.setXOption(mob.getObjectId());
                        tsb.setYOption(skillID);
                        tsb.setExpireTerm(1080);
                        tsm.putCharacterStatValue(CharacterTemporaryStat.死亡標記, tsb.getOption());
                        tsm.sendSetStatPacket();
                    }
                }
                break;
            case 狂風:
                o.nOption = 1;
                o.rOption = skill.getSkillId();
                o.tOption = si.getValue(SkillStat.time, slv);
                for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    if (mob == null) {
                        continue;
                    }
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    mts.addStatOptionsAndBroadcast(MobStat.Stun, o);
                }
                break;
            case 創造_1:
            case 創造_2:
            case 創造_3:
            case 追蹤:
                if (hasHitMobs) {
                    int chance = 100;
                    if (skillID == 追蹤) {
                        chance = 15;
                    }
                    List<MobAttackInfo> mai = attackInfo.mobAttackInfo;
                    if (attackInfo.mobAttackInfo.size() <= 0) {
                        return;
                    }
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(Util.getRandomFromCollection(mai).mobId);
                    if (mob == null) {
                        return;
                    }
                    if (Util.succeedProp(chance)) {
                        spawnAetherShard(mob.getPosition());
                    }
                }
                break;
            case 魔劍共鳴:
            case 魔劍共鳴_1:
                si = SkillData.getSkillInfoById(魔劍共鳴_2);
                int amount = 1;
                if (tsm.hasStat(CharacterTemporaryStat.魔劍共鳴)) {
                    amount = tsm.getOption(CharacterTemporaryStat.魔劍共鳴).xOption;
                    if (amount < 3) {
                        amount++;
                    }
                }
                o.nValue = si.getValue(SkillStat.z, slv) * amount;
                o.nReason = 魔劍共鳴_2;
                o.tTerm = si.getValue(SkillStat.time, slv);
                tsm.putCharacterStatValue(CharacterTemporaryStat.IndieIgnoreMobpdpR, o);
                o1.nOption = amount; //idk
                o1.xOption = amount;
                o1.yOption = si.getValue(SkillStat.y, slv) * amount;
                o1.rOption = 魔劍共鳴_2;
                o1.tOption = si.getValue(SkillStat.time, slv) * 2;
                tsm.putCharacterStatValue(CharacterTemporaryStat.魔劍共鳴, o1);
                tsm.sendSetStatPacket();
                break;
        }
        super.handleAttack(c, attackInfo);
    }

    public void createCapeAtoms() {
        ArrayList<SecondAtom> atomToCreate = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            int atomObjectID = chr.getField().getNewObjectID();
            final SecondAtom capeAtom =
                    new SecondAtom(atomObjectID,
                            chr.getId(),
                            0,
                            0,
                            創造,
                            chr.getSkillLevel(創造),
                            chr.getPosition(),
                            SecondAtomEnum.SwordCape.getVal() /*+ getCapeAtoms().size() */ + i,
                            capeAtoms.size(),
                            0,
                            65,
                            0,
                            1320,
                            0,
                            false,
                            null);

            atomToCreate.add(capeAtom);
            capeAtoms.put(capeAtoms.size(), atomObjectID);
            chr.createSecondAtom(atomToCreate);
            atomToCreate.clear();
        }
    }

    public void removeCapeAtoms() {
        capeAtoms.forEach((key, objectID) -> {
            chr.getField().broadcastPacket(SecondAtomPacket.removeSecondAtom(chr, objectID));
        });
        capeAtoms.clear();

        chr.setSecondAtomKeyCounter(1); // not sure what this is for
    }

    public void spawnAetherShard(Position pos) {
        Skill skill = chr.getSkill(乙太結晶);
        SkillInfo si = null;
        if (skill != null) {
            si = SkillData.getSkillInfoById(乙太結晶);
        }
        Summon summon = Summon.getSummonByNoCTS(c.getChr(), si.getSkillId(), si.getCurrentLevel());
        summon.setPosition(pos);
        summon.setMoveAction((byte) 4);
        summon.setCurFoothold((short) 0);
        summon.setMoveAbility(MoveAbility.Stop);
        summon.setAssistType(AssistType.None);
        summon.setEnterType(EnterType.NoAnimation);
        summon.setFlyMob(false);
        summon.setBeforeFirstAttack(true);
        summon.setAttackActive(true);
        summon.setSummonTerm(30);

        summonList.add(summon);
        if (summonList.size() > 7) {
            chr.getField().removeLife(summonList.get(0));
            summonList.remove(0);
        }

        chr.getField().spawnAddSummon(summon);
    }

    @Override
    public void handleSkill(Char chr, TemporaryStatManager tsm, int skillID, int slv, InPacket inPacket, SkillUseInfo skillUseInfo) {
        List<SecondAtom> seconAtoms = new LinkedList();
        super.handleSkill(chr, tsm, skillID, slv, inPacket, skillUseInfo);
        Rect rect = chr.getRectAround(new Rect(-600, -600, 600, 600));
        int mobObjId;
        Field field = chr.getField();
        Skill skill = chr.getSkill(skillID);
        SkillInfo si = null;
        if (chr.getSkill(skillID) != null) {
            si = SkillData.getSkillInfoById(skillID);
        }
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        switch (skillID) {
            case 創造:
                if (tsm.hasStatBySkillId(skillID)) {
                    tsm.removeStatsBySkill(skillID);
                    removeCapeAtoms();
                } else {
                    o1.nOption = 1;
                    o1.rOption = skillID;
                    o1.tOption = si.getValue(SkillStat.time, slv);
                    tsm.putCharacterStatValue(CharacterTemporaryStat.創造, o1);
                    createCapeAtoms();
                }
                break;
            case 奇蹟:
                if (tsm.hasStatBySkillId(skillID)) {
                    tsm.removeStatsBySkill(skillID);
                }  else {
                    o1.nOption = 1;// si.getValue(SkillStat.x, slv); ?? 8 in wz 1 in sniff?
                    o1.rOption = skillID;
                    tsm.putCharacterStatValue(CharacterTemporaryStat.奇蹟, o1);
                }
                break;
            case 推進器:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Booster, o1);
                tsm.sendSetStatPacket();
                break;
            case 雷普的勇士:
                o1.nReason = skillID;
                o1.nValue = si.getValue(x, slv);
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieStatR, o1);
                /*
                o1.rOption = skillID;
                o1.nOption = si.getValue(SkillStat.x, slv);
                o1.tOption = si.getValue(SkillStat.time, slv);
                tsm.putCharacterStatValue(CharacterTemporaryStat.BasicStatUp, o1);
                 */
                break;
            case 雷普勇士的意志:
                tsm.removeAllDebuffs();
                break;
            case 劍域:
                Summon summon = Summon.getSummonBy(c.getChr(), skillID, slv);
                summon.setFlyMob(false);
                summon.setSummonTerm(si.getValue(SkillStat.time, slv));
                summon.setMoveAbility(MoveAbility.Stop);
                field.spawnSummon(summon);
                break;
            case 碎片:
                if (!chr.isLeft()) {
                    rect = rect.horizontalFlipAround(chr.getPosition().getX());
                }
                for (int i = 0; i < 5; i++) {
                    List<Mob> lifes = field.getMobsInRect(rect);
                    if (lifes.size() <= 0) {
                        return;
                    }
                    Mob mob = Util.getRandomFromCollection(field.getMobsInRect(rect));

                    final SecondAtom fa = new SecondAtom(
                            chr.getField().getNewObjectID(),
                            chr.getId(),
                            mob.getObjectId(),
                            (i == 1) ? 15 : ((i == 2) ? -15 : ((i == 3) ? 30 : ((i == 4) ? -30 : 0))),
                            碎片,
                            chr.getSkillLevel(碎片),
                            chr.getPosition(),
                            SecondAtomEnum.MagicShard.getVal(),
                            chr.getNewSecondAtomKey(),
                            0,
                            1,
                            i < 1 ? 0 : i < 3 ? 120 : 240,
                            600,
                            2400,
                            false,
                            null);

                    seconAtoms.add(fa);
                }
                chr.createSecondAtom(seconAtoms, true);
                seconAtoms.clear();
                break;
            case 追蹤:
                if (!chr.isLeft()) {
                    rect = rect.horizontalFlipAround(chr.getPosition().getX());
                }
                for (int i = 0; i < 2; i++) {
                    List<Mob> lifes = field.getMobsInRect(rect);
                    if (lifes.size() <= 0) {
                        return;
                    }
                    Mob mob = Util.getRandomFromCollection(field.getMobsInRect(rect));

                    final SecondAtom fa = new SecondAtom(
                            chr.getField().getNewObjectID(),
                            chr.getId(),
                            mob.getObjectId(),
                            0,
                            追蹤,
                            chr.getSkillLevel(追蹤),
                            rect.getRandomPositionInside(),
                            SecondAtomEnum.FlyingSword.getVal(),
                            chr.getNewSecondAtomKey(),
                            0,
                            80,
                            0,
                            1000,
                            40000,
                            false,
                            null);

                    seconAtoms.add(fa);
                }
                chr.createSecondAtom(seconAtoms, true);
                seconAtoms.clear();
                modifyAether(-100);
                break;
            case 無限:
                o1.nValue = 1;
                o1.nReason = 15112;
                o1.tTerm = 3;
                tsm.putCharacterStatValue(CharacterTemporaryStat.IndieNotDamaged, o1);

                for (int i = 0; i < 18; i++) {
                    List<Mob> lifes = field.getMobsInRect(rect);
                    if (lifes.size() <= 0) {
                        return;
                    }
                    Mob mob = Util.getRandomFromCollection(field.getMobsInRect(rect));

                    final SecondAtom fa = new SecondAtom(
                            chr.getField().getNewObjectID(),
                            chr.getId(),
                            0,
                            0,
                            無限,
                            chr.getSkillLevel(無限),
                            rect.getRandomPositionInside(),
                            SecondAtomEnum.RedFlyingSword.getVal(),
                            chr.getNewSecondAtomKey(),
                            0,
                            65,
                            0,
                            1320,
                            31000,
                            false,
                            null);

                    seconAtoms.add(fa);
                }
                chr.createSecondAtom(seconAtoms, true);
                seconAtoms.clear();
                break;
            case 復原:
                o1.nReason = skillID;
                o1.nValue = si.getValue(SkillStat.indieDamR, slv);
                o1.tTerm = si.getValue(SkillStat.time, slv);
                tsm.putCharacterStatValue(CharacterTemporaryStat.IndieDamR, o1);
                o2.rOption = skillID;
                o2.nOption = 1;
                o2.tOption = si.getValue(SkillStat.time, slv);
                tsm.putCharacterStatValue(CharacterTemporaryStat.高潔精神, o1);//?
                break;
            case 神之種族:
                o1.nReason = skillID;
                o1.nValue = si.getValue(indieDamR, slv);
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieDamR, o1);
                break;
            case 高潔精神:
                o1.rOption = skillID;
                o1.nOption = 10; //Sniff
                o1.tOption = si.getValue(SkillStat.time, slv);
                o1.xOption = si.getValue(SkillStat.x, slv);
                o1.yOption = si.getValue(SkillStat.y, slv);
                tsm.putCharacterStatValue(CharacterTemporaryStat.高潔精神, o1);
                break;
            case 魔力爆裂:
                if (skillUseInfo.spawnCrystals) break;
                List<Rect> shardRects = new ArrayList<>();
                for (Tuple<Integer, Position> shard : skillUseInfo.shardsPositions) {
                    Position shardPosition = shard.getRight();
                    Rect shardRect = shardPosition.getRectAround(si.getLastRect());
                    shardRects.add(shardRect);
                    AffectedArea aa = AffectedArea.getPassiveAA(chr, 魔力爆裂, slv);
                    aa.setSkillID(魔力爆裂);
                    aa.setPosition(shard.getRight());
                    aa.setDelay((short) 3);
                    aa.setDuration(1590);
                    aa.setRect(shard.getRight().getRectAround(si.getLastRect()));
                    aa.setHitMob(shard.getLeft() != 0);
                    chr.getField().spawnAffectedArea(aa);
                }
                chr.write(UserLocal.adeleShardBreakerResult(魔力爆裂, shardRects));//?
                break;
            case 乙太結晶:
                spawnAetherShard(skillUseInfo.endingPosition);
                break;
            case 懸浮:
                o1.nValue = 50;
                o1.nReason = skillID;
                o1.tTerm = si.getValue(SkillStat.time, slv);
                tsm.putCharacterStatValue(IndieFloating, o1);
                o2.nOption = 50;
                o2.rOption = skillID;
                o2.tOption = si.getValue(time, slv) / 1000;// 1900/1000
                tsm.putCharacterStatValue(NewFlying, o2);
                tsm.sendSetStatPacket();
                break;
            case 魔劍共鳴:
                chr.getField().removeLife(skillUseInfo.objectId, true);
                break;
        }
        tsm.sendSetStatPacket();
    }

    @Override
    public void handleHit(Client c, InPacket inPacket, HitInfo hitInfo) {
        super.handleHit(c, inPacket, hitInfo);
    }

    @Override
    public void setCharCreationStats(Char chr) {
        super.setCharCreationStats(chr);
        CharacterStat cs = chr.getAvatarData().getCharacterStat();
        cs.setLevel(10);
        cs.setStr(4);
        cs.setDex(4);
        cs.setInt(4);
        cs.setLuk(4);
    }

    @Override
    public int getFinalAttackSkill() {
        return 0;
    }

    @Override
    public void handleLevelUp() {
        super.handleLevelUp();
        short level = chr.getLevel();
        switch (level) {
            case 10:
                handleJobAdvance(JobConstants.JobEnum.ADELE_1.getJobId());
                break;
            case 30:
                handleJobAdvance(JobConstants.JobEnum.ADELE_2.getJobId());
                break;
            case 60:
                handleJobAdvance(JobConstants.JobEnum.ADELE_3.getJobId());
                break;
            case 100:
                handleJobAdvance(JobConstants.JobEnum.ADELE_4.getJobId());
                break;
        }
    }

    @Override
    public void handleJobStart() {
        super.handleJobStart();
        handleJobAdvance(JobConstants.JobEnum.ADELE_1.getJobId());
        chr.addSpToJobByCurrentJob(3);
        handleJobEnd();
    }

    @Override
    public void handleJobEnd() {
        super.handleJobEnd();
        //chr.forceUpdateSecondary(null, ItemData.getItemDeepCopy(1353600)); // Initial Path (2ndary)

        Item secondary = ItemData.getItemDeepCopy(1354000); // unk
        if (secondary != null) {
            chr.addItemToInventory(secondary);
        }
    }
}
