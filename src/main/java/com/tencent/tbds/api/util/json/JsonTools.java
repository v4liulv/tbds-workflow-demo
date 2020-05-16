package com.tencent.tbds.api.util.json;

import java.io.*;
import java.util.Objects;

/**
 * @author liulv
 * @description
 */
public class JsonTools {
    /**
     * 单位缩进字符串。
     */
    private static String SPACE = "   ";

    /**
     * 返回格式化JSON字符串。
     *
     * @param json 未格式化的JSON字符串。
     * @return 格式化的JSON字符串。
     */
    public static String formatJson(String json) {
        StringBuffer result = new StringBuffer();

        int length = json.length();
        int number = 0;
        char key = 0;
        //遍历输入字符串。
        for (int i = 0; i < length; i++) {
            //1、获取当前字符。
            key = json.charAt(i);

            //2、如果当前字符是前方括号、前花括号做如下处理：
            if ((key == '[') || (key == '{')) {
                //（1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
                if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
                    result.append('\n');
                    result.append(indent(number));
                }

                //（2）打印：当前字符。
                result.append(key);

                //（3）前方括号、前花括号，的后面必须换行。打印：换行。
                result.append('\n');

                //（4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
                number++;
                result.append(indent(number));

                //（5）进行下一次循环。
                continue;
            }

            //3、如果当前字符是后方括号、后花括号做如下处理：
            if ((key == ']') || (key == '}')) {
                //（1）后方括号、后花括号，的前面必须换行。打印：换行。
                result.append('\n');

                //（2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
                number--;
                result.append(indent(number));

                //（3）打印：当前字符。
                result.append(key);


                //（4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
                if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
                    //result.append('\n');
                }

                //（5）继续下一次循环。
                continue;
            }

            //4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
            if ((key == ',')) {
                result.append(key);
                result.append('\n');
                result.append(indent(number));
                continue;
            }

            //5、打印：当前字符。
            result.append(key);
        }

        return result.toString();
    }

    /**
     * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
     *
     * @param number 缩进次数。
     * @return 指定缩进次数的字符串。
     */
    private static String indent(int number) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < number; i++) {
            result.append(SPACE);
        }
        return result.toString();
    }

    public static String readToString(String fileName) {
        String propsPath;
        try {
            propsPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath() + File.separator;
        }catch (Exception e){
            throw new RuntimeException("读取classpath目录异常");
        }
        if(fileName == null || fileName.equals(""))
            throw new RuntimeException("json文件名为空");

        String encoding = "UTF-8";
        File file = new File(propsPath + fileName);
        long fileLength = file.length();
        byte[] fileContent = new byte[(int) fileLength];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(fileContent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    public static void writerIntoFile(String filePath, String fileName, String response) {
        if(fileName == null || fileName.equals(""))
            throw new RuntimeException("json文件名为空");

        String encoding = "UTF-8";
        File file = new File(filePath + File.separator + fileName);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(response.getBytes(encoding));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream!=null)
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JsonTools json = new JsonTools();
        String str = "{\"MessageSequence\":1575535321489,\"To\":\"010000220000\",\"RequestParam\":{\"RegisterInfo\":[{\"ResourceInfo\":{\"SJZZCCFZX\":\"\",\"SJZYMS\":\"人口测试\",\"SJZY_SSMLBBH\":\"3.0.0\",\"SJZYGLDW_GAJGJGDM\":\"530000000000\",\"SJZYBSF\":\"R-530000000000-00000122\",\"ZYZTDM\":\"1\",\"SJZYMC\":\"人口测试\",\"SJZZYJFL\":\"01\",\"SJZYSQDW_SQDWDM\":\"530000000000\",\"ZCSJ\":\"Nov 28,2019 9:58:9 AM\",\"SJZYLYLX\":\"01003  公安执法与执勤数据/治安管理\",\"SJZYBQ2\":\"1\",\"SJZYGXZQ\":\"GXZQ_N\",\"SJZYBQ1\":\"1\",\"SJZYBQ4\":\"2\",\"SJZYBQ3\":\"3\",\"SJXJBM\":\"ZEB-41322300006\",\"SJXJZWMC\":\"人口测试\",\"SJHQFS\":\"01\",\"ZLSJJLGM\":\"\",\"SJZYCCZQ\":\"GXZQ_N\",\"YYXTBH\":\"A-530000000000-ZNB-00001\",\"SJZYMLBH\":\"D-530000000000-ZEB-41322300006\",\"SJZYWZ\":\"01\",\"CLSJJLGM\":\"\",\"CLSJCCGM\":\"\",\"SFBZML\":\"true\",\"SJZZEJFL\":\"01\"},\"DataInfo\":[{\"SFBSX\":\"0\",\"SSMLSJXBH\":\"ZEB-41322300006-018\",\"SFBT\":\"1\",\"SJXBH\":\"\",\"SJXBSF\":\"\",\"SJYNBBSF\":\"\",\"SJZYBH\":\"D-530000000000-ZEB-41322300006\",\"ZCSJ\":\"May 03,2017 9:49:5 AM\",\"SJXZWMC\":\"公民身份号码\",\"SJZYBSF\":\"R-530000000000-00000122\",\"SFSX\":\"1\",\"SJXYWMC\":\"PID\"},{\"SFBSX\":\"0\",\"SSMLSJXBH\":\"ZEB-41322300006-011\",\"SFBT\":\"1\",\"SJXBH\":\"\",\"SJXBSF\":\"\",\"SJYNBBSF\":\"\",\"SJZYBH\":\"D-530000000000-ZEB-41322300006\",\"ZCSJ\":\"May 03,2017 9:49:5 AM\",\"SJXZWMC\":\"办理单位名称\",\"SJZYBSF\":\"R-530000000000-00000122\",\"SFSX\":\"1\",\"SJXYWMC\":\"WHO_IN_UNIT_NAME\"},{\"SFBSX\":\"0\",\"SSMLSJXBH\":\"ZEB-41322300006-017\",\"SFBT\":\"1\",\"SJXBH\":\"\",\"SJXBSF\":\"\",\"SJYNBBSF\":\"\",\"SJZYBH\":\"D-530000000000-ZEB-41322300006\",\"ZCSJ\":\"May 03,2017 9:49:5 AM\",\"SJXZWMC\":\"父亲姓名\",\"SJZYBSF\":\"R-530000000000-00000122\",\"SFSX\":\"1\",\"SJXYWMC\":\"FA_NAME\"}]}]},\"From\":\"530000000000\",\"InfoType\":\"catalog-publish-report\"}";


       String result = json.formatJson(str);

        System.out.println(result);

    }
}
