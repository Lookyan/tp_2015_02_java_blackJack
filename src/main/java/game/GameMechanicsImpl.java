package game;

import interfaces.GameMechanics;

/**
 * <--start-- player
 * --cards--> player
 * if (currentPhase == bet)
 *     --bet--> player
 *
 * for each player i:
 *     --bet--> i (enable buttons) (parallel)
 *     <--int-- i
 *     [(--error-->i;<--int-- i),]
 *     --ok--> i
 *     copy to playing
 *     --int--> every j != i
 *     --end--> i (disable all buttons)
 *
 * for each player i:
 *     --play--> i
 *     приходит hit
 *     отсылаем всем карту
 *     отылаем end если превысили 21 иначе ok
 *     приходит stand
 *     отсылаем end (а может и нет) (не надо?)
 *
 */

public class GameMechanicsImpl implements GameMechanics {


    @Override
    public void addUser(String sessionId) {

    }

    @Override
    public void hit(String sessionId) {

    }

    @Override
    public void stand(String sessionId) {

    }
}
