package eu.cryptoeuro.fixture;

import eu.cryptoeuro.rest.model.Account

public class AccountFixture {

    public static Account sampleAccount() {
        return new Account(
                "0x65fa6548764c08c0dd77495b33ed302d0c212691",
                true,
                false,
                false,
                3L,
                100L
        )
    }

}
