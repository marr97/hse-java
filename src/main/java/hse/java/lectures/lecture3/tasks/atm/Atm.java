package hse.java.lectures.lecture3.tasks.atm;

import java.util.*;

public class Atm {
    public enum Denomination {
        D50(50),
        D100(100),
        D500(500),
        D1000(1000),
        D5000(5000);

        private final int value;

        Denomination(int value) {
            this.value = value;

        }

        int value() {
            return value;
        }

        public static Denomination fromInt(int value) {
            return Arrays.stream(values()).filter(v -> v.value == value)
                    .findFirst()
                    .orElse(null);
        }
    }

    private final Map<Denomination, Integer> banknotes = new EnumMap<>(Denomination.class);

    public Atm() {
    }

    public void deposit(Map<Denomination, Integer> banknotes){
        if (banknotes ==  null) {
            throw new InvalidDepositException("cannot deposit nothing\n");
        }

        for (Map.Entry<Denomination, Integer> entry: banknotes.entrySet()) {
            Denomination denomination = entry.getKey();
            Integer count = entry.getValue();

            if (count <= 0) {
                throw new InvalidDepositException("cannot deposit <= 0!\n");
            }
            this.banknotes.put(denomination, count);
        }
    }

    public Map<Denomination, Integer> withdraw(int amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("negative or zero amount!\n");
        } else {
            if (amount > getBalance()) {
                throw new InsufficientFundsException("cannot withdraw more than balance!\n");
            }
        }

        int remains = amount;
        Map<Denomination, Integer> res = new HashMap<>();
        List<Denomination> sortedDenominations = new ArrayList<>(banknotes.keySet());
        sortedDenominations.sort((a, b) -> Integer.compare(b.value(), a.value()));

        for (Denomination denomination: sortedDenominations) {
            Integer count = banknotes.get(denomination);

            if (remains >= denomination.value() && count > 0) {
                int neededCnt = remains / denomination.value();
                int CountGive = Math.min(neededCnt, count);

                if (CountGive > 0) {
                    res.put(denomination, CountGive);
                }
                remains -= CountGive * denomination.value();
            }
        }

        if (remains > 0) {
            throw new CannotDispenseException("cannot withdraw!\n");
        }

        res.forEach((denomination, count) -> {
            banknotes.put(denomination, banknotes.get(denomination) - count);
        });

        return res;
    }

    public int getBalance() {
        int sum = 0;
        for (Map.Entry<Denomination, Integer> entry: banknotes.entrySet()) {
            Denomination denomination = entry.getKey();
            Integer count = entry.getValue();
            sum += denomination.value() * count;

        }
        return sum;
    }

}
