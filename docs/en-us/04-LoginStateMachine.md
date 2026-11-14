> [!NOTE] Previous · [03-Lifecycle](./03-Lifecycle.md) · [PluginDocs](../PluginDocs.md) · [Next](./05-Event-System.md)

# 4. LoginStateMachine

Some servers require custom login flows (captcha, human verification, SMS, etc.). The `LoginStateMachine` API lets you declare state transitions as a chain of method calls — no boilerplate `if/else` graphs.

## States

`LoginState` is an enum with six values:

| State | Meaning |
|-------|---------|
| `IDLE` | Initial state; also the reset state after kick |
| `REGISTER` | Player registering |
| `VERIFY` | Human verification / bot check |
| `LOGIN` | Logging in |
| `JOIN` | Success — joined the main server |
| `DISCONNECTED` | Disconnected (final unless reset) |

## Builder chain

```java
import org.angellock.impl.api.state.LoginStateMachine;
import org.angellock.impl.api.state.LoginState;

@Override
public void onEnable(RobotPlayer bot) {
    new LoginStateMachine()
        .source(LoginState.IDLE)
        .whenReceive("Please log in")
        .goal(LoginState.LOGIN, loginAction)

        .source(LoginState.LOGIN)
        .whenReceive("Login successful")
        .goal(LoginState.JOIN, joinAction)

        .resetOnlyWhen(KickReason.HUMAN_VERIFICATION)
        .build();
}
```

### Methods

| Method | Purpose |
|--------|---------|
| `.source(LoginState)` | Precondition state for the transition block |
| `.whenReceive(String)` | Server-message regex trigger |
| `.goal(LoginState, Action)` | Target state + action to run |
| `.and()` | Branch — same source, alternative trigger |
| `.resetOnlyWhen(KickReason)` | If kicked with this reason, restart from `IDLE` |
| `.build()` | Compile the chain — call **once at the end** |

## Branching with `.and()`

A state can transition to multiple targets — the first matching trigger wins:

```java
new LoginStateMachine()
    .source(LoginState.IDLE)
        .whenReceive("Please verify").goal(LoginState.VERIFY, verifyAction)
        .and()                          // ← same source, second branch
        .whenReceive("Please log in").goal(LoginState.LOGIN, loginAction)

    .source(LoginState.VERIFY)
        .whenReceive("Verified").goal(LoginState.REGISTER, registerAction)

    .source(LoginState.LOGIN)
        .whenReceive("Login successful").goal(LoginState.JOIN, joinAction)

    .resetOnlyWhen(KickReason.HUMAN_VERIFICATION)
    .build();
```

This declares:
- From `IDLE`: if server says *"Please verify"* → verify → register; else if *"Please log in"* → login → join
- From `VERIFY`: *"Verified"* → register
- From `LOGIN`: *"Login successful"* → join
- If kicked with `KickReason.HUMAN_VERIFICATION` → restart from `IDLE`

> [!TIP] String matching
> `whenReceive` does substring matching, not regex. Keep triggers short and unambiguous.

## When to use it

Use `LoginStateMachine` **only** when you need multi-step flows that depend on server messages. For simple "send this packet on join" scenarios, a plain `LoginHandler` in `getListeners()` is enough.
