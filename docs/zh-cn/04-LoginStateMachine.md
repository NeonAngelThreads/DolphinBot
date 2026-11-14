> [!NOTE] 上一页 · [03-生命周期](./03-生命周期.md) · [PluginDocs_CN](../PluginDocs_CN.md) · [下一页](./05-事件系统.md)

# 4. LoginStateMachine

某些服务器有自定义登录流程（验证码、人机验证、短信等）。`LoginStateMachine` API 让你用链式调用声明状态转换——不需要写一堆 `if/else` 图。

## 状态枚举

`LoginState` 包含六个值：

| 状态 | 含义 |
|------|------|
| `IDLE` | 初始状态；被踢出后的重置状态 |
| `REGISTER` | 玩家注册 |
| `VERIFY` | 人机验证 / Bot 检测 |
| `LOGIN` | 登录中 |
| `JOIN` | 成功加入主服务器 |
| `DISCONNECTED` | 断开连接（final 除非重置） |

## Builder 链

```java
import org.angellock.impl.api.state.LoginStateMachine;
import org.angellock.impl.api.state.LoginState;

@Override
public void onEnable(RobotPlayer bot) {
    new LoginStateMachine()
        .source(LoginState.IDLE)
        .whenReceive("请登录")
        .goal(LoginState.LOGIN, loginAction)

        .source(LoginState.LOGIN)
        .whenReceive("登录成功")
        .goal(LoginState.JOIN, joinAction)

        .resetOnlyWhen(KickReason.HUMAN_VERIFICATION)
        .build();
}
```

## 方法速查

| 方法 | 用途 |
|------|------|
| `.source(LoginState)` | 当前转换的前置状态 |
| `.whenReceive(String)` | 服务器消息子串触发 |
| `.goal(LoginState, Action)` | 目标状态 + 执行的动作 |
| `.and()` | 分支——同一 source 的备选触发条件 |
| `.resetOnlyWhen(KickReason)` | 被特定原因踢出时从 IDLE 重启 |
| `.build()` | 编译链路——**必须在最后调用一次** |

## 用 `.and()` 做分支

一个状态可以转换到多个目标——先匹配到的触发优先：

```java
new LoginStateMachine()
    .source(LoginState.IDLE)
        .whenReceive("请验证").goal(LoginState.VERIFY, verifyAction)
        .and()                          // ← 同一 source，第二个分支
        .whenReceive("请登录").goal(LoginState.LOGIN, loginAction)

    .source(LoginState.VERIFY)
        .whenReceive("验证通过").goal(LoginState.REGISTER, registerAction)

    .source(LoginState.LOGIN)
        .whenReceive("登录成功").goal(LoginState.JOIN, joinAction)

    .resetOnlyWhen(KickReason.HUMAN_VERIFICATION)
    .build();
```

含义：
- IDLE：服务器说"请验证"→ 验证 → 注册；否则如果"请登录"→ 登录 → 加入
- VERIFY："验证通过"→ 注册
- LOGIN："登录成功"→ 加入
- 被人机验证踢出 → 从 IDLE 重启

> [!TIP] 字符串匹配
> `whenReceive` 做子串匹配，不是正则。保持触发短语短且无歧义。

## 什么时候用它

**只**在需要依赖服务器消息的多步流程时使用。简单的"加入时发送某个包"场景，用普通的 `LoginHandler` 订阅包就够了。
