/*
 A plugin that adds functionality for restoring prayer at altars.

 SUPPORTS:
  -> Recharging prayer at altars.

 TODO:
  -> Add more altar object identifiers.

 AUTHOR: lare96
*/

import io.luna.game.event.impl.ObjectFirstClickEvent
import io.luna.game.model.mobile.Skill.PRAYER
import io.luna.game.model.mobile.{Animation, Player}


/* A set of identifiers for altar objects. */
private val ALTARS = Set(409, 3243)

/* Recharge prayer animation. */
private val ANIMATION = new Animation(645)


/* A method that attempts to recharge the player's prayer. */
private def rechargePrayer(plr: Player) = {
  val skill = plr.skill(PRAYER)

  if (skill.getLevel < skill.getStaticLevel) {
    skill.setLevel(skill.getStaticLevel)

    plr.animation(ANIMATION)
    plr.sendMessage("You recharge your Prayer points.")
  } else {
    plr.sendMessage("You already have full Prayer points.")
  }
}


/* If the object being clicked is an altar, recharge prayer. */
intercept[ObjectFirstClickEvent] { (msg, plr) =>
  if (ALTARS.contains(msg.getId)) {
    rechargePrayer(plr)
    msg.terminate
  }
}
