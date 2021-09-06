package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleCharacter.DelayedQuestUpdate;
import client.MapleClient;
import client.MapleQuestStatus;
import net.AbstractPacketHandler;
import net.packet.InPacket;
import scripting.quest.QuestScriptManager;
import server.quest.MapleQuest;

/**
 *
 * @author Xari
 */
public class RaiseUIStateHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, MapleClient c) {
        int infoNumber = p.readShort();
        
        if (c.tryacquireClient()) {
            try {
                MapleCharacter chr = c.getPlayer();
                MapleQuest quest = MapleQuest.getInstanceFromInfoNumber(infoNumber);
                MapleQuestStatus mqs = chr.getQuest(quest);
                
                QuestScriptManager.getInstance().raiseOpen(c, (short) infoNumber, mqs.getNpc());
                
                if (mqs.getStatus() == MapleQuestStatus.Status.NOT_STARTED) {
                    quest.forceStart(chr, 22000);
                    c.getAbstractPlayerInteraction().setQuestProgress(quest.getId(), infoNumber, 0);
                } else if (mqs.getStatus() == MapleQuestStatus.Status.STARTED) {
                    chr.announceUpdateQuest(DelayedQuestUpdate.UPDATE, mqs, mqs.getInfoNumber() > 0);
                }
            } finally {
                c.releaseClient();
            }
        }
    }
}