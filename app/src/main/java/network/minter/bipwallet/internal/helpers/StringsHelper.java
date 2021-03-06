/*
 * Copyright (C) by MinterTeam. 2018
 * @link <a href="https://github.com/MinterTeam">Org Github</a>
 * @link <a href="https://github.com/edwardstock">Maintainer Github</a>
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package network.minter.bipwallet.internal.helpers;

import java.util.List;

import network.minter.bipwallet.R;
import network.minter.bipwallet.internal.Wallet;


public class StringsHelper {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static String boolToString(Boolean b) {
        if (b == null || !b) {
            return Wallet.app().res().getString(R.string.no);
        }

        return Wallet.app().res().getString(R.string.yes);
    }

    public static String glue(List<String> input, String glue) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.size(); i++) {
            sb.append(input.get(i));
            if (i + 1 < input.size()) {
                sb.append(glue);
            }
        }

        return sb.toString();
    }

    public static String repeat(char c, int n) {
        if (n <= 0) {
            return "";
        }

        final StringBuilder out = new StringBuilder();
        for (int i = 0; i < n; i++) {
            out.append(c);
        }

        return out.toString();
    }
}
