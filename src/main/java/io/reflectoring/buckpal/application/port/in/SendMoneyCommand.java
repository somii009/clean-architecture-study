package io.reflectoring.buckpal.application.port.in;

import com.sun.istack.NotNull;
import io.reflectoring.buckpal.domain.Account.AccountId;
import io.reflectoring.buckpal.domain.Money;
import lombok.Getter;

@Getter
public class SendMoneyCommand {

    @NotNull
    private final AccountId sourceAccountId;

    @NotNull
    private final AccountId targetAccountId;

    @NotNull
    private final Money money;

    public SendMoneyCommand(
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money money) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;ㅇ
        this.money = money;
        requireGreaterThan(money, 0);
    }
    )
}
