package mobile.data.usage.spyspyyou.gametest.game.events.local;


import mobile.data.usage.spyspyyou.gametest.game.Game;

public class SkillActivationEvent extends LocalEvent {

    public SkillActivationEvent(){}

    @Override
    public void apply(Game game) {
        game.activateSkill();
    }
}
