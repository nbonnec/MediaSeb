/*
 * Copyright 2016 nbonnec
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nbonnec.mediaseb.account;

public class AccountUtils {
    /**
     * Card number must be 14 character length.
     * @param cardNumber card number.
     * @return true if too short.
     */
    public static boolean isCardNumberTooShort(String cardNumber) {
        return cardNumber.length() < 14;
    }

    /**
     * Only numbers.
     * @param cardNumber card number.
     * @return true if correct.
     */
    public static boolean isCardNumberCorrect(String cardNumber) {
        return cardNumber.matches("[0-9]+");
    }

}
