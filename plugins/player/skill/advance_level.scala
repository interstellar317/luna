/*
 A plugin that adds functionality for advancing skill levels.

 SUPPORTS:
   -> Fireworks, congratulations interface, etc.
   -> Recalculation of combat level when combat skills change.
   -> Queuing messages for level and experience changes.

 AUTHOR: lare96
*/

import io.luna.game.event.impl.SkillChangeEvent
import io.luna.game.model.mobile.update.UpdateFlagHolder.UpdateFlag
import io.luna.game.model.mobile.{Graphic, Player, Skill}
import io.luna.util.StringUtils


/* Graphic played when a player advances a level. */
val LEVEL_UP_GRAPHIC = new Graphic(199)

/* A table that contains data for displaying the level up chat box interface. */
val LEVEL_UP_TABLE = Vector(
  (6248, 6249, 6247),
  (6254, 6255, 6253),
  (6207, 6208, 6206),
  (6217, 6218, 6216),
  (5453, 6114, 4443),
  (6243, 6244, 6242),
  (6212, 6213, 6211),
  (6227, 6228, 6226),
  (4273, 4274, 4272),
  (6232, 6233, 6231),
  (6259, 6260, 6258),
  (4283, 4284, 4282),
  (6264, 6265, 6263),
  (6222, 6223, 6221),
  (4417, 4438, 4416),
  (6238, 6239, 6237),
  (4278, 4279, 4277),
  (4263, 4264, 4261),
  (12123, 12124, 12122),
  (4889, 4890, 4887),
  (4268, 4269, 4267)
)


/* Determine if a player has advanced a level, and if they have, notify them. */
def advanceLevel(plr: Player, id: Int, oldLevel: Int) = {
  val set = plr.getSkills
  val skill = plr.skill(id)
  val newLevel = skill.getStaticLevel

  if (oldLevel < newLevel) {
    skill.setLevel(if (id != Skill.HITPOINTS) newLevel else skill.getLevel + 1)

    val (firstLineId, secondLineId, interfaceId) = LEVEL_UP_TABLE(id)
    val name = Skill.getName(id)
    val message = s"Congratulations, you just advanced ${StringUtils.computeIndefiniteArticle(name)} $name level!"

    plr.sendMessage(message)
    plr.sendWidgetText(message, firstLineId)
    plr.sendWidgetText(s"Your $name level is now $newLevel.", secondLineId)
    plr.sendChatboxInterface(interfaceId)

    plr.graphic(LEVEL_UP_GRAPHIC)

    if (Skill.isCombatSkill(id)) {
      set.resetCombatLevel()
      plr.flag(UpdateFlag.APPEARANCE)
    }
  }
}


/* When a player's skills change, send the update to the client and check if they've advanced a level. */
intercept_@[SkillChangeEvent](TYPE_PLAYER) { (msg, plr) =>
  plr.sendSkillUpdate(msg.getId)

  if (msg.getOldStaticLevel < 99) {
    advanceLevel(plr, msg.getId, msg.getOldStaticLevel)
  }
}