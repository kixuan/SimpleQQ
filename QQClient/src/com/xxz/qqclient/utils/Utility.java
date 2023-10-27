package com.xxz.qqclient.utils;

import java.util.Scanner;

/**
 * @author kixuan
 * @version 1.0
 */
public class Utility {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * 功能：读取键盘输入的指定长度的字符串(不能为空)
     *
     * @param limit 限制的长度
     * @return 指定长度的字符串
     */

    public static String readString(int limit) {
        return readKeyBoard(limit, false);
    }

    /**
     * 功能： 读取一个字符串
     *
     * @param limit       读取的长度
     * @param blankReturn 如果为true ,表示 可以读空字符串。
     *                    如果为false表示 不能读空字符串。
     *                    如果输入为空，或者输入大于limit的长度，就会提示重新输入。
     */
    private static String readKeyBoard(int limit, boolean blankReturn) {
        String line = "";
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();

            if (line.isEmpty()) {
                if (blankReturn) return line;
                else continue;
            }

            if (line.length() > limit) {
                System.out.println("输入长度（不大于" + limit + "）错误，请重新输入：");
                continue;
            }
            break;
        }
        return line;
    }
}
