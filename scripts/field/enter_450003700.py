# id 450003700 (Lachelein : Lachelein Riverside), field 450003700
sm.lockInGameUI(True, False)
sm.blind(True, 255, 0, 0, 0, 0)
sm.bgmVolume(100, 100)
sm.forcedAction(25, 30000)
sm.blind(True, 255, 0, 0, 0, 0)
sm.sendDelay(1200)
sm.blind(False, 0, 0, 0, 0, 1000)
sm.sendDelay(1400)
sm.sendDelay(1000)
sm.sendDelay(2000)
sm.speechBalloon(True, 0, 0, "Yawn... Hey, Flying Fish,\r\n how far have we gone?", 1500, 0, 0, 0, 0, 4, 0, 4878499)
sm.showFadeTransition(0, 1500, 3000)
sm.zoomCamera(0, 2000, 0, 446, 281)
sm.sendDelay(300)
sm.removeOverlapScreen(1500)
sm.sendDelay(2000)
sm.showEffect("Map/Effect3.img/Lacheln/smallB", 0, 0, 0, 0, 0, 0, 0)
sm.changeBGM("Bgm46.img/ClockTowerofNightmare", 0, 0)
sm.sendDelay(5000)
sm.sendDelay(2000)
sm.speechBalloon(True, 0, 0, "I feel... strangely drowsy...", 1500, 0, 0, 0, 0, 4, 0, 4878499)
sm.spineScreen(True, False, False, 0, "Map/Effect3.img/BossLucid/butterfly/005","animation","")
sm.sendDelay(1000)
sm.blind(True, 255, 0, 0, 0, 2500)
sm.sendDelay(5000)
sm.bgmVolume(0, 5000)
sm.sendDelay(5000)
sm.changeBGM("Bgm00.img/Silence", 0, 0)
sm.lockInGameUI(False, True)
sm.warp(450003000)