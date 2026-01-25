package org.angellock.impl.extensions.examples;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.state.Action;
import org.angellock.impl.state.LoginState;
import org.angellock.impl.state.LoginStateMachine;
import org.angellock.impl.events.handlers.LoginHandler;
import org.angellock.impl.plugin.AbstractPlugin;
import org.angellock.impl.util.reason.KickReason;

public class ExamplePlugin extends AbstractPlugin {
    protected LoginState initialState = LoginState.IDLE;

    @Override
    public String getPluginName() {
        return "My First Plugin";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Hello DolphinBot";
    }

    @Override
    public void onDisable() {
        //Disable Message
    }

    @Override
    public void onLoad() {
        // Loading Plugin Message
    }

    @Override
    public void onEnable(AbstractRobot entityBot) {

        Action verifyAction = new Action(entityBot) {
            @Override
            public void execute() {
                // verify logic
            }
        };
        Action registerAction = new Action(entityBot) {
            @Override
            public void execute() {
                // login logic
            }
        };
        Action joinAction = new Action(entityBot) {
            @Override
            public void execute() {
                // join logic
            }
        };
        Action loginAction = new Action(entityBot) {
            @Override
            public void execute() {
                // login logic
            }
        };

        LoginStateMachine stateMachine = new LoginStateMachine(this.initialState);

        stateMachine
                .source(LoginState.IDLE).whenReceive("请进行机器人验证").goal(LoginState.VERIFY, verifyAction)
                .and()
                    .whenReceive("请登录").goal(LoginState.LOGIN, loginAction)
                .source(LoginState.VERIFY).whenReceive("机器人验证已完毕").goal(LoginState.REGISTER, registerAction)
                .source(LoginState.REGISTER).whenReceive("已成功注册").goal(LoginState.JOIN, joinAction)
                .source(LoginState.LOGIN).whenReceive("已成功登录").goal(LoginState.JOIN, joinAction)
                .resetOnlyWhen(KickReason.HUMAN_VERIFICATION).build();

        getListeners().add(
                new LoginHandler().addExtraAction((loginPacket) -> {
                    getLogger().info(loginPacket.getCommonPlayerSpawnInfo().getGameMode().name());
                })
        );
    }
}
