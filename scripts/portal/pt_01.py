from net.swordie.ms.client.character.skills.temp import CharacterTemporaryStat

field = {
    141060000 : 141010000   # Middle of Strait : Ice Station 1
}

portal = {
    141060000 : 0
}

def init():
    currentmap = sm.getFieldID()
    warp = True

    if currentmap == 141060000:
        # Ride Vehicle Check
        if sm.getnOptionByCTS(CharacterTemporaryStat.RideVehicle) == 1930000: # Riena Skiff used in Riena Strait
            sm.removeCTS(CharacterTemporaryStat.RideVehicle)

    if warp:
        sm.warp(field[currentmap], portal[currentmap])
        sm.dispose()