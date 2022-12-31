package net.swordie.ms.connection.packet;

import net.swordie.ms.client.character.Char;
import net.swordie.ms.connection.OutPacket;
import net.swordie.ms.handlers.header.OutHeader;
import net.swordie.ms.util.Randomizer;

public class SkillPacket {

    public static OutPacket CreateSubObtacle(Char chr, int skillid) {
        OutPacket outPacket = new OutPacket(OutHeader.FAMILIAR_HIT);
        outPacket.encodeInt(chr.getId());
        outPacket.encodeInt(5);
        for (int i = 1; i <= 5; i++) {
            outPacket.encodeInt(i);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(i-1);
            outPacket.encodeInt(chr.getId());
            outPacket.encodeInt(0);
            outPacket.encodeInt((i == 2 || i == 3) ? 120 : ((i == 4 || i == 5) ? 240 : 0));
            outPacket.encodeInt(600);
            outPacket.encodeInt((i == 2) ? 15 : ((i == 3) ? -15 : ((i == 4) ? 30 : ((i == 5) ? -30 : 0))));
            outPacket.encodeInt(skillid);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(2400);
            outPacket.encodeInt(0);
            outPacket.encodeInt(1);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt((i == 1) ? chr.getPosition().getX() : ((i == 2) ? (chr.getPosition().getX() + 40) : ((i == 3) ? (chr.getPosition().getX() - 40) : ((i == 4) ? (chr.getPosition().getX() + 80) : ((i == 5) ? (chr.getPosition().getX() - 80) : 0)))));
            outPacket.encodeInt((i == 1) ? (chr.getPosition().getY() - 110) : ((i == 2 || i == 3) ? (chr.getPosition().getY() - 100) : ((i == 4 || i == 5) ? (chr.getPosition().getY() - 90) : 0)));
            outPacket.encodeByte(0);
            outPacket.encodeByte(0);
            outPacket.encodeByte(0);
            outPacket.encodeInt(0);
        }
        outPacket.encodeInt(0);
        return outPacket;
    }

    public static OutPacket CreateSwordReadyObtacle(Char chr, int skillid, int count) {
        OutPacket outPacket = new OutPacket(OutHeader.FAMILIAR_HIT);
        outPacket.encodeInt(chr.getId());
        outPacket.encodeInt(2);
        for (int i = 1; i <= 2; i++) {
            outPacket.encodeInt((i == 1) ? ((count - 1) * 10) : (count * 10));
            outPacket.encodeInt(0);
            outPacket.encodeInt((i == 1) ? (count - 1) : count);
            outPacket.encodeInt(0);
            outPacket.encodeInt(chr.getId());
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(skillid);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(1);
            outPacket.encodeInt(0);
            outPacket.encodeInt(0);
            outPacket.encodeInt(chr.getPosition().getX());
            outPacket.encodeInt(1);
            outPacket.encodeByte(0);
            outPacket.encodeByte(0);
            outPacket.encodeByte(0);
            outPacket.encodeInt(0);
        }
        outPacket.encodeInt(0);
        return outPacket;
    }

    public static OutPacket removeSecondAtom(Char chr, int count) {
        OutPacket outPacket = new OutPacket(OutHeader.FAMILIAR_TRANSFER_FIELD);
        outPacket.encodeInt(chr.getId());
        outPacket.encodeInt(1);
        outPacket.encodeInt(count);
        outPacket.encodeInt(0);
        outPacket.encodeInt(2);
        return outPacket;
    }

    public static OutPacket AutoAttackObtacleSword(Char chr,final int sword, final int id) {
        OutPacket outPacket = new OutPacket(OutHeader.FAMILIAR_ATTACK);
        outPacket.encodeInt(chr.getId());
        outPacket.encodeInt(sword);
        if (id == 6 || id == 5) {
            outPacket.encodeInt(3);
        } else if (id == 4 || id == 3) {
            outPacket.encodeInt(2);
        } else if (id == 2 || id == 1) {
            outPacket.encodeInt(1);
        } else {
            outPacket.encodeInt(0);
        }
        return outPacket;
    }

    public static OutPacket 穿刺(Char chr, int skillId, int level) {
        OutPacket outPacket = new OutPacket(OutHeader.穿刺);
        outPacket.encodeByte(1);
        outPacket.encodeInt(skillId);
        outPacket.encodeInt(level);
        outPacket.encodeInt(1);
        outPacket.encodeInt(1);
        return outPacket;
    }
}
