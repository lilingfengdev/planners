package com.bh.planners.core.kether

import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

class ActionSkill {

    companion object {

        @KetherParser(["skill"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            it.switch {
                case("level") {
                    actionNow {
                        getSession().playerSkill.level
                    }
                }
                case("name") {
                    actionNow {
                        getSession().playerSkill.instance.option.name
                    }
                }


            }

        }

    }
}
