package client.jobs.legend;

import client.Client;
import client.character.Char;
import client.character.HitInfo;
import client.character.skills.*;
import client.field.Field;
import client.jobs.Job;
import client.life.AffectedArea;
import client.life.Mob;
import connection.InPacket;
import constants.JobConstants;
import enums.ChatMsgColour;
import enums.ForceAtomEnum;
import loaders.SkillData;
import packet.CField;
import packet.UserLocal;
import packet.WvsContext;
import util.Position;
import util.Rect;
import util.Util;

import java.util.Arrays;
import java.util.Random;

import static client.character.skills.CharacterTemporaryStat.*;
import static client.character.skills.SkillStat.*;

/**
 * Created on 12/14/2017.
 */
public class Phantom extends Job {

    public static final int JUDGMENT_DRAW_1 = 20031209;
    public static final int JUDGMENT_DRAW_2 = 20031210;

    public static final int SKILL_SWIPE = 20031207;
    public static final int LOADOUT = 20031208;
    public static final int TO_THE_SKIES = 20031203;
    public static final int DEXTEROUS_TRAINING = 20030206;

    public static final int IMPECCABLE_MEMORY_I = 24001001; //TODO

    public static final int IMPECCABLE_MEMORY_II = 24101001; //TODO
    public static final int CANE_BOOSTER = 24101005; //Buff
    public static final int CARTE_BLANCHE = 24100003;

    public static final int IMPECCABLE_MEMORY_III = 24111001; //TODO
    public static final int FINAL_FEINT = 24111002; //Buff (Unlimited Duration) Gone upon Death
    public static final int BAD_LUCK_WARD = 24111003; //Buff
    public static final int CLAIR_DE_LUNE = 24111005; //Buff

    public static final int IMPECCABLE_MEMORY_IV = 24121001; //TODO
    public static final int PRIERE_DARIA = 24121004; //Buff
    public static final int VOL_DAME = 24121007; // Special Buff TODO
    public static final int MAPLE_WARRIOR_PH = 24121008; //Buff
    public static final int CARTE_NOIR = 24120002;              //80001890
    public static final int HEROS_WILL_PH = 24121009;

    public static final int HEROIC_MEMORIES_PH = 24121053;
    public static final int CARTE_ROSE_FINALE = 24121052;

    public static final int CARTE_ATOM = 80001890; //TODO maybe

    private int[] addedSkills = new int[] {
            JUDGMENT_DRAW_2,
            SKILL_SWIPE,
            LOADOUT,
            TO_THE_SKIES,
            DEXTEROUS_TRAINING,
    };

    private final int[] buffs = new int[] {
            IMPECCABLE_MEMORY_I,
            IMPECCABLE_MEMORY_II,
            CANE_BOOSTER,
            IMPECCABLE_MEMORY_III,
            FINAL_FEINT,
            BAD_LUCK_WARD,
            CLAIR_DE_LUNE,
            IMPECCABLE_MEMORY_IV,
            PRIERE_DARIA,
            VOL_DAME,
            MAPLE_WARRIOR_PH,
            HEROIC_MEMORIES_PH,
    };

    private int cardAmount;

    public Phantom(Char chr) {
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
        switch (skillID) {
            case CANE_BOOSTER:
                o1.nOption = si.getValue(x, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(Booster, o1);
                c.write(UserLocal.incJudgementStack((byte) 15));
                break;
            case FINAL_FEINT:
                //TODO
                break;
            case BAD_LUCK_WARD:
                o1.nValue = si.getValue(indieMhpR, slv);
                o1.nReason = skillID;
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMHPR, o1);
                o2.nValue = si.getValue(indieMmpR, slv);
                o2.nReason = skillID;
                o2.tStart = (int) System.currentTimeMillis();
                o2.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieMMPR, o2);
                o3.nOption = si.getValue(x, slv);
                o3.rOption = skillID;
                o3.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(AsrR, o3);
                o4.nOption = si.getValue(y, slv);
                o4.rOption = skillID;
                o4.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(TerR, o4);
                break;
            case CLAIR_DE_LUNE:
                o1.nValue = si.getValue(indiePad, slv);
                o1.nReason = skillID;
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndiePAD, o1);
                o2.nValue = si.getValue(indieAcc, slv);
                o2.nReason = skillID;
                o2.tStart = (int) System.currentTimeMillis();
                o2.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieACC, o2);
                break;
            case PRIERE_DARIA:
                o1.nOption = si.getValue(damR, slv);
                o1.rOption = skillID;
                o1.tOption = si.getValue(time, slv);
                tsm.putCharacterStatValue(DamR, o1);
                o2.nOption = si.getValue(ignoreMobpdpR, slv);
                o2.rOption = skillID;
                o2.tOption = si.getValue(time , slv);
                tsm.putCharacterStatValue(IgnoreMobpdpR, o2);
                break;
            case MAPLE_WARRIOR_PH:
                o1.nValue = si.getValue(x, slv);
                o1.nReason = skillID;
                o1.tStart = (int) System.currentTimeMillis();
                o1.tTerm = si.getValue(time, slv);
                tsm.putCharacterStatValue(IndieStatR, o1);
                break;
            case HEROIC_MEMORIES_PH:
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
        }
        c.write(WvsContext.temporaryStatSet(tsm));
    }

    private void handleCarte(AttackInfo attackInfo) {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        //if (chr.hasSkill(CARTE_BLANCHE)) {
            SkillInfo si = SkillData.getSkillInfoById(CARTE_BLANCHE);
            int anglenum = new Random().nextInt(30) + 295;
            for (MobAttackInfo mai : attackInfo.mobAttackInfo) {
                Mob mob = (Mob) chr.getField().getLifeByObjectID(mai.mobId);
                int TW1prop = 100;//  SkillData.getSkillInfoById(SOUL_SEEKER_EXPERT).getValue(prop, slv);   //TODO Change
                if (Util.succeedProp(TW1prop)) {
                    if (chr.hasSkill(CARTE_NOIR)) {
                        int mobID = mai.mobId;
                        int inc = ForceAtomEnum.PHANTOM_CARD_2.getInc();    //TODO doesn't show the CarteNoir Image,  shows Carte Blanche
                        int type = ForceAtomEnum.PHANTOM_CARD_2.getForceAtomType();
                        ForceAtomInfo forceAtomInfo = new ForceAtomInfo(1, inc, 20, 35,
                                anglenum, 0, (int) System.currentTimeMillis(), 1, 0,
                                new Position()); //Slightly behind the player
                        chr.getField().broadcastPacket(CField.createForceAtom(false, 0, chr.getId(), type,
                                true, mobID, CARTE_NOIR, forceAtomInfo, new Rect(), 0, 300,
                                mob.getPosition(), CARTE_NOIR, mob.getPosition())); //TODO NPE on Mille
                    } else if (chr.hasSkill(CARTE_BLANCHE)) {
                        int mobID = mai.mobId;
                        int inc = ForceAtomEnum.PHANTOM_CARD_1.getInc();
                        int type = ForceAtomEnum.PHANTOM_CARD_1.getForceAtomType();
                        ForceAtomInfo forceAtomInfo = new ForceAtomInfo(1, inc, 20, 40,
                                anglenum, 0, (int) System.currentTimeMillis(), 1, 0,
                                new Position()); //Slightly behind the player
                        chr.getField().broadcastPacket(CField.createForceAtom(false, 0, chr.getId(), type,
                                true, mobID, CARTE_BLANCHE, forceAtomInfo, new Rect(), 0, 300,
                                mob.getPosition(), CARTE_BLANCHE, mob.getPosition())); //TODO NPE on Mille
                    }
                }
            }
        //}
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
        //handleCardDeck(skill.getSkillId(), tsm, c);
        if(hasHitMobs) {
            handleCarte(attackInfo);
        }
        handleDeck();
        Option o1 = new Option();
        Option o2 = new Option();
        Option o3 = new Option();
        switch (attackInfo.skillId) {
            case CARTE_ROSE_FINALE:
                AffectedArea aa = AffectedArea.getAffectedArea(chr, attackInfo);
                aa.setMobOrigin((byte) 1);
                aa.setPosition(chr.getPosition());
                aa.setRect(aa.getPosition().getRectAround(si.getRects().get(0)));
                chr.getField().spawnAffectedArea(aa);
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
                case VOL_DAME:
                    //TODO
                    break;
                case IMPECCABLE_MEMORY_I:
                case IMPECCABLE_MEMORY_II:
                case IMPECCABLE_MEMORY_III:
                case IMPECCABLE_MEMORY_IV:
              //case IMPECCABLE_MEMORY_H
                    //TODO
                    break;
                case TO_THE_SKIES:
                    o1.nValue = si.getValue(x, slv);
                    Field toField = c.getChannelInstance().getField(o1.nValue);
                    chr.warp(toField);
                    break;
                case JUDGMENT_DRAW_1:
                case JUDGMENT_DRAW_2:
                    resetCardStack();
                    break;
                case HEROS_WILL_PH:
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
        JobConstants.JobEnum job = JobConstants.JobEnum.getJobById(id);
        switch (job) {
            case PHANTOM:
            case PHANTOM1:
            case PHANTOM2:
            case PHANTOM3:
            case PHANTOM4:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getFinalAttackSkill() {
        return 0;
    }

    private void handleDeck() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        Option o = new Option();
        cardAmount = 1;
        if(tsm.hasStat(PDD)) {
            cardAmount = tsm.getOption(PDD).nOption;
            if(cardAmount < getMaxCards()) {
                cardAmount++;
            }
        }
        o.nOption = cardAmount;
        o.rOption = 0;
        o.tOption = 0;
        tsm.putCharacterStatValue(PDD, o);
        c.write(WvsContext.temporaryStatSet(tsm));
        c.write(UserLocal.incJudgementStack((byte) cardAmount));
    }

    private int getMaxCards() {
        int num = 0;
        if(chr.hasSkill(JUDGMENT_DRAW_1)) {
            num = 20;
        }
        if(chr.hasSkill(JUDGMENT_DRAW_2)) {
            num = 40;
        }
        return num;
    }

    private void resetCardStack() {
        TemporaryStatManager tsm = chr.getTemporaryStatManager();
        cardAmount = 0;
        tsm.removeStat(PDD, true);
        c.write(WvsContext.temporaryStatReset(tsm, false));
    }
}