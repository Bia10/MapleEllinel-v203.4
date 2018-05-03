package client.jobs.legend;

import client.Client;
import client.character.Char;
import client.character.HitInfo;
import client.character.skills.*;
import client.field.Field;
import client.jobs.Job;
import client.life.Mob;
import client.life.MobTemporaryStat;
import client.life.Summon;
import connection.InPacket;
import constants.JobConstants;
import constants.SkillConstants;
import enums.ChatMsgColour;
import enums.LeaveType;
import enums.MobStat;
import enums.MoveAbility;
import loaders.SkillData;
import packet.CField;
import packet.WvsContext;
import util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static client.character.skills.CharacterTemporaryStat.*;
import static client.character.skills.SkillStat.*;

/**
 * Created on 12/14/2017.
 */
public class Mercedes extends Job {
    //Link Skill = return skill

    public static final int ELVEN_GRACE = 20020112;
    public static final int UPDRAFT = 20020111;
    public static final int ELVEN_HEALING = 20020109;

    public static final int DUAL_BOWGUN_BOOSTER = 23101002; //Buff

    public static final int STUNNING_STRIKES = 23111000; //Special Attack
    public static final int UNICORN_SPIKE = 23111002; //Special Attack
    public static final int IGNIS_ROAR = 23111004; //Buff
    public static final int WATER_SHIELD = 23111005; //Buff
    public static final int ELEMENTAL_KNIGHTS_BLUE = 23111008; //Summon
    public static final int ELEMENTAL_KNIGHTS_RED = 23111009; //Summon
    public static final int ELEMENTAL_KNIGHTS_PURPLE = 23111010; //Summon

    public static final int SPIKES_ROYALE = 23121002;  //Special Attack
    public static final int STAGGERING_STRIKES = 23120013; //Special Attack
    public static final int ANCIENT_WARDING = 23121004; //Buff
    public static final int MAPLE_WARRIOR_MERC = 23121005; //Buff
    public static final int LIGHTNING_EDGE = 23121003; //Debuff mobs
    public static final int HEROS_WILL_MERC = 23121008;

    public static final int HEROIC_MEMORIES_MERC = 23121053;
    public static final int ELVISH_BLESSING = 23121054;

    //Final Attack
    public static final int FINAL_ATTACK_DBG = 23100006;
    public static final int ADVANCED_FINAL_ATTACK = 23120012;

    private int[] addedSkills = new int[] {
            ELVEN_GRACE,
            UPDRAFT,
            ELVEN_HEALING,
    };

    private final int[] buffs = new int[]{
            DUAL_BOWGUN_BOOSTER,
            IGNIS_ROAR,
            WATER_SHIELD,
            ELEMENTAL_KNIGHTS_BLUE, //Summon
            ELEMENTAL_KNIGHTS_RED, //Summon
            ELEMENTAL_KNIGHTS_PURPLE, //Summon
            ANCIENT_WARDING,
            MAPLE_WARRIOR_MERC,
            HEROIC_MEMORIES_MERC,
            ELVISH_BLESSING,
    };

    private final int[] summonAttacks = new int[] {
            ELEMENTAL_KNIGHTS_BLUE,
            ELEMENTAL_KNIGHTS_RED,
            ELEMENTAL_KNIGHTS_PURPLE,
    };

    private int eleKnightSummonID = 1;
    private int eleKnightAmount = 1;
    private int lastAttackSkill = 0;
    private HashMap<Integer,Summon> eleKnightSummon = new HashMap<>();
    private List<Summon> summonList = new ArrayList<>();

    public Mercedes(Char chr) {
        super(chr);
        if(isHandlerOfJob(chr.getJob())) {
            for (int id : addedSkills) {
                if (!chr.hasSkill(id)) {
                    Skill skill = SkillData.getSkillDeepCopyById(id);
                    skill.setCurrentLevel(skill.getMasterLevel());
                    chr.addSkill(skill);
                }
            }
        }
    }

    public void handleBuff(Client c, InPacket inPacket, int skillID, byte slv) {
        Char chr = c.getChr();
        SkillInfo si = SkillData.getSkillInfoById(skillID);
        TemporaryStatManager tsm = c.getChr().getTemporaryStatManager();
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        Option o4 = new Option();
        Summon summon;
        Field field;
        switch (skillID) {
            case DUAL_BOWGUN_BOOSTER:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Booster, o1);
                break;
            case IGNIS_ROAR:
                o1.nOption = 1;
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(IgnisRore, o1);
                o2.nValue = si.getValue(indiePad, slv);
                o2.nReason = skillID;
                o2.tStart = (int) System.currentTimeMillis();
                o2.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndiePAD, o2);
                break;
            case WATER_SHIELD:
                o1.nOption = si.getValue(asrR, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(AsrR, o1);
                o2.nOption = si.getValue(terR, slv);
                o2.rOption = skillID;
                o2.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(TerR, o2);
                o3.nOption = si.getValue(x, slv);
                o3.rOption = skillID;
                o3.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(DamAbsorbShield, o3);   //IgnoreMobDamR
                break;
            case ANCIENT_WARDING:
                o1.nOption = si.getValue(emhp, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(EMHP, o1);
                o2.nValue = si.getValue(indiePadR, slv);
                o2.nReason = skillID;
                o2.tStart = (int) System.currentTimeMillis();
                o2.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndiePADR, o2);
                break;
            case MAPLE_WARRIOR_MERC:
                o1.nValue = si.getValue(x, slv);
                o1.nReason = skillID;
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieStatR, o1);
                break;
            case HEROIC_MEMORIES_MERC:
                o1.nReason = skillID;
                o1.nValue = si.getValue(indieDamR, slv);
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieDamR, o1);
                o2.nReason = skillID;
                o2.nValue = si.getValue(indieMaxDamageOverR, slv);
                o2.tStart = (int) System.currentTimeMillis();
                o2.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMaxDamageOverR, o2);
                break;
            case ELVISH_BLESSING:
                o1.nValue = si.getValue(indiePad, slv);
                o1.nReason = skillID;
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndiePAD, o1);
                o2.nOption = si.getValue(x, slv);
                o2.rOption = skillID;
                o2.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Stance, o2);
                break;
            case ELEMENTAL_KNIGHTS_BLUE:
                summonEleKnights();
                break;
        }
        c.write(WvsContext.temporaryStatSet(tsm));
    }

    private void handleIgnisRoar(int skillID, TemporaryStatManager tsm, Client c, AttackInfo attackInfo) {
        if (Arrays.asList(summonAttacks).contains(skillID)) {
            return;
        } else {
            Option o = new Option();
            Option o1 = new Option();
            Option o2 = new Option();
            SkillInfo ignisRoarInfo = SkillData.getSkillInfoById(IGNIS_ROAR);
            Skill skill = chr.getSkill(IGNIS_ROAR);
            byte slv = (byte) skill.getCurrentLevel();
            int amount = 1;
            if(attackInfo.skillId == getFinalAttackSkill()) {
                return;
            }
            if(attackInfo.skillId == lastAttackSkill) {
                return;
            }
            if (tsm.hasStat(IgnisRore)) {
                if (tsm.hasStat(AddAttackCount)) {
                    amount = tsm.getOption(AddAttackCount).nOption;
                    if (amount < ignisRoarInfo.getValue(y, slv)) {
                        amount++;
                    }
                }

                lastAttackSkill = attackInfo.skillId;
                o.nOption = amount;
                o.rOption = IGNIS_ROAR;
                o.tOption = ignisRoarInfo.getValue(subTime, slv);
                tsm.putCharacterStatValue(AddAttackCount, o);
                o1.nOption = (amount * ignisRoarInfo.getValue(x, slv));
                o1.rOption = IGNIS_ROAR;
                o1.tOption = ignisRoarInfo.getValue(subTime, slv);
                tsm.putCharacterStatValue(DamR, o1);
            }
            c.write(WvsContext.temporaryStatSet(tsm));
        }
    }

    public boolean isBuff(int skillID) {
        return Arrays.stream(buffs).anyMatch(b -> b == skillID);
    }

    @Override
    public void handleAttack(Client c, AttackInfo attackInfo) {
        Char chr = c.getChr();
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Skill skill = chr.getSkill(attackInfo.skillId);
        int skillID = 0;
        SkillInfo si = null;
        boolean hasHitMobs = attackInfo.mobAttackInfo.size() > 0;
        byte slv = 0;
        if (skill != null) {
            si = SkillData.getSkillInfoById(skill.getSkillId());
            slv = (byte) skill.getCurrentLevel();
            skillID = skill.getSkillId();
        }
        if (hasHitMobs) {
            handleIgnisRoar(SkillConstants.getActualSkillIDfromSkillID(skillID), tsm, c, attackInfo);
        }
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        switch (attackInfo.skillId) {
            case STUNNING_STRIKES:
                SkillInfo stunningStrikes = SkillData.getSkillInfoById(STUNNING_STRIKES);
                int proc = stunningStrikes.getValue(prop, slv);
                for(MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    if(Util.succeedProp(proc)) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        o1.nOption = 1;
                        o1.rOption = STUNNING_STRIKES;
                        o1.tOption = si.getValue(time, slv);
                        mts.addStatOptionsAndBroadcast(MobStat.Stun, o1);
                    }
                }
                break;
            case STAGGERING_STRIKES:
                SkillInfo staggeringStrikes = SkillData.getSkillInfoById(STAGGERING_STRIKES);
                int procc = staggeringStrikes.getValue(prop, slv);
                for(MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    if(Util.succeedProp(procc)) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        o1.nOption = 1;
                        o1.rOption = STAGGERING_STRIKES;
                        o1.tOption = si.getValue(time, slv);
                        mts.addStatOptionsAndBroadcast(MobStat.Stun, o1);
                    }
                }
                break;
            case UNICORN_SPIKE:
                for(MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    if(Util.succeedProp(si.getValue(prop, slv))) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        o1.nOption = si.getValue(x, slv);
                        o1.rOption = skill.getSkillId();
                        o1.tOption = si.getValue(time, slv);
                        mts.addStatOptionsAndBroadcast(MobStat.AddDamParty, o1); // ?
                    }
                }
                break;
            case SPIKES_ROYALE:
                for(MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o1.nOption = - si.getValue(x, slv);
                    o1.rOption = skill.getSkillId();
                    o1.tOption = si.getValue(time, slv);
                    mts.addStatOptionsAndBroadcast(MobStat.PDR, o1);
                    mts.addStatOptionsAndBroadcast(MobStat.MDR, o1);
                }
                break;
            case LIGHTNING_EDGE:
                for(MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    o1.nOption = si.getValue(x, slv);
                    o1.rOption = skill.getSkillId();
                    o1.tOption = si.getValue(time, slv);
                    mts.addStatOptionsAndBroadcast(MobStat.AddDamSkill, o1);
                }
                break;
            case ELEMENTAL_KNIGHTS_BLUE:
                for(MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    if(Util.succeedProp(si.getValue(prop, slv))) {
                        Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                        MobTemporaryStat mts = mob.getTemporaryStat();
                        o1.nOption = 1;
                        o1.rOption = skill.getSkillId();
                        o1.tOption = si.getValue(subTime, slv);
                        mts.addStatOptionsAndBroadcast(MobStat.Freeze, o1);
                    }
                }
                break;
            case ELEMENTAL_KNIGHTS_RED:
                for(MobAttackInfo mai : attackInfo.mobAttackInfo) {
                    Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                    MobTemporaryStat mts = mob.getTemporaryStat();
                    mts.createAndAddBurnedInfo(chr.getId(), skill, 1);
                }
                break;
        }
    }

    @Override
    public void handleSkill(Client c, int skillID, byte slv, InPacket inPacket) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Char chr = c.getChr();
        Skill skill = chr.getSkill(skillID);
        SkillInfo si = null;
        if(skill != null) {
            si = SkillData.getSkillInfoById(skillID);
        }
        chr.chatMessage(ChatMsgColour.YELLOW, "SkillID: " + skillID);
        if (isBuff(skillID)) {
            handleBuff(c, inPacket, skillID, slv);
        } else {
            Option o1 = new Option();
            Option o2 = new Option();
            Option o3 = new Option();
            switch(skillID) {
                case HEROS_WILL_MERC:
                    tsm.removeAllDebuffs();
                    break;
            }
        }
    }

    @Override
    public void handleHit(Client c, InPacket inPacket, HitInfo hitInfo) {

        super.handleHit(c, inPacket, hitInfo);
    }

    @Override
    public boolean isHandlerOfJob(short id) {
        return JobConstants.isMercedes(id);
    }

    @Override
    public int getFinalAttackSkill() {
        if(Util.succeedProp(getFinalAttackProc())) {
            int fas = 0;
            if (chr.hasSkill(FINAL_ATTACK_DBG)) {
                fas = FINAL_ATTACK_DBG;
            }
            if (chr.hasSkill(ADVANCED_FINAL_ATTACK)) {
                fas = ADVANCED_FINAL_ATTACK;
            }
            return fas;
        } else {
            return 0;
        }
    }

    private Skill getFinalAtkSkill(Char chr) {
        Skill skill = null;
        if(chr.hasSkill(FINAL_ATTACK_DBG)) {
            skill = chr.getSkill(FINAL_ATTACK_DBG);
        }
        if(chr.hasSkill(ADVANCED_FINAL_ATTACK)) {
            skill = chr.getSkill(ADVANCED_FINAL_ATTACK);
        }
        return skill;
    }

    private int getFinalAttackProc() {
        Skill skill = getFinalAtkSkill(chr);
        if(skill == null) {
            return 0;
        }
        SkillInfo si = SkillData.getSkillInfoById(skill.getSkillId());
        byte slv = (byte) chr.getSkill(skill.getSkillId()).getCurrentLevel();
        int proc = si.getValue(prop, slv);

        return proc;
    }

    private void summonKnights(byte slv) {
        List<Integer> set = new ArrayList<>();
        set.add(23111008);
        set.add(23111009);
        set.add(23111010);

        if(eleKnightSummonID != 0) {
            set.remove((Integer) eleKnightSummonID);
        }

        int random = Util.getRandomFromList(set);
        eleKnightSummonID = random;
        Summon summon = Summon.getSummonBy(chr, random, slv);
        Field field = chr.getField();
        summon.setFlyMob(true);
        summon.setMoveAbility(MoveAbility.FLY_AROUND_CHAR.getVal());
        field.spawnSummon(summon);
        eleKnightAmount++;
    }

    private void summonEleKnights() {
        List<Integer> set = new ArrayList<>();
        set.add(23111008);
        set.add(23111009);
        set.add(23111010);

        if(eleKnightSummonID != 0) {
            set.remove((Integer) eleKnightSummonID);
        }
        int random = Util.getRandomFromList(set);
        eleKnightSummonID = random;
        Summon summon = Summon.getSummonBy(chr, random, (byte) 1);
        Field field = chr.getField();
        summon.setMoveAbility(MoveAbility.FLY_AROUND_CHAR.getVal());

        summonList.add(summon);
        if(summonList.size() > 2) {
            c.write(CField.summonedRemoved(summonList.get(0), LeaveType.ANIMATION));
            summonList.remove(0);
        }

        field.spawnSummon(summon);
    }
}
