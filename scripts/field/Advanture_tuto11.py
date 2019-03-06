# Maple Road : Maple Tree Hill (4000011)  Map for Advanture Tutorial

UNK_NPC = 10300# "???""

if sm.hasQuest(32202):
    sm.dispose()
else:
    sm.createQuestWithQRValue(32217, "1", False)
    sm.lockInGameUI(True)
    sm.showFieldEffect("maplemap/enter/10000", 0)
    sm.sendDelay(1000)


    sm.spawnNpc(UNK_NPC, -240, 220)
    sm.showNpcSpecialActionByTemplateId(UNK_NPC, "summon", 0)
    sm.showEffect("Effect/Direction12.img/effect/tuto/BalloonMsg1/1", 900, 0, -120, 0, sm.getNpcObjectIdByTemplateId(UNK_NPC), False, 0)
    sm.sendDelay(1800)

    sm.moveNpcByTemplateId(UNK_NPC, False, 1000, 100)
    sm.moveCamera(False, 200, 200, 200)

    sm.moveCamera(True, 0, 0, 0)

    sm.sendDelay(900)

    sm.setSpeakerID(0)
    sm.setSpeakerType(3)
    sm.removeEscapeButton()
    sm.setPlayerAsSpeaker()
    sm.sendNext("Who was that girl? Why did she run away when she saw me?")
    sm.sendNext("Maybe I'll follow her..")

    sm.removeNpc(UNK_NPC)
    sm.startQuest(32202)
    sm.completeQuest(32202)
    sm.lockInGameUI(False)
