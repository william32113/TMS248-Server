# id 910090350 (Partem : Deathly Abyss), field 910090350
sm.lockInGameUI(True, False)
sm.blind(True, 255, 0, 0, 0, 0)
sm.createQuestWithQRValue(35927, "count=0")
sm.createQuestWithQRValue(35927, "count=0;talk1=0")
sm.createQuestWithQRValue(35927, "count=0;talk1=0;talk2=0")
sm.forcedMove(True, 10)
sm.forcedInput(7)
sm.sendDelay(300)
sm.forcedInput(7)
sm.sendDelay(300)
sm.forcedMove(True, 100)
sm.blind(True, 255, 0, 0, 0, 0)
sm.sendDelay(1200)
sm.blind(False, 0, 0, 0, 0, 1000)
sm.sendDelay(1400)
sm.lockInGameUI(False, True)