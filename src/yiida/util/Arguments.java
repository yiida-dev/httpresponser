package yiida.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * "オプション"と"値"の形式を持つ引数を表すクラスです�??
 * アプリケーションのmainエントリポイントに渡されるString配�?�を解析すると想定されて�?ます�??
 * こ�?�クラスはString配�?�を入力として、以下�?�ルールに従って解析します�??
 * <br/><br/>
 * �?つの�?字�?�を"オプション"また�?�"値"また�?�"オプション"+"値"であると解釈します�??
 * オプションは"-"で始まり�?�それに続く1�?字以上�?�名前を持ちます�??
 * オプションではな�?�?字�?�とオプション以外�?�部�?�?字�?��?�すべて値となります�??
 * オプションは値を持つことができ�?�オプションに続く値をそのオプションの値と認識します�??
 * 続く値、とは同じ�?字�?��??のオプションの次の値もしく�?�次の�?字�?�が値であった�?�合�?�そ�?�値です�??
 * 続く値が無�?場合�?�オプションは値を持ちません�?
 * これはオプションの名前の解釈�?�仕方により、同じ文字�?�でも�?数の"オプション"と"値"になることを意味します�??
 * たとえ�?��?"-abc"と�?�?�?字�?�とそ�?�次に"xyz"と�?�?�?字�?�があった�?�合�??
 * "-a"オプションの値は"bc"�?
 * "-ab"オプションの値は"d"�?
 * "-abc"オプションの値は"xyz"となります�??
 * @author bp.iida.yuuji
 *
 */
public final class Arguments {

    private List<String> optionAndValues;
    private Map<String, String> optionValues;
    private List<String> singleValues;
    public Arguments(String[] args) {
        optionValues = new HashMap<String, String>();
        singleValues = new ArrayList<String>();
        optionAndValues = new ArrayList<String>();
        boolean lastOption = false;
        for(int i = 0; i < args.length; i++) {
            if(args[i] == null) continue;
            if(args[i].length() >= 2 && args[i].startsWith("-")) {
                optionAndValues.add(args[i].substring(1));
                lastOption = true;
            } else {
                if(lastOption) {
                    optionValues.put(optionAndValues.get(optionAndValues.size() - 1), args[i]);
                } else {
                    singleValues.add(args[i]);
                }
                lastOption = false;
            }
        }
    }

    /**
     * こ�?�引数が指定された名前(先�?�の"-"を含めな�?)のオプションを持つかど�?かを取得します�??
     * @param name オプションの名前
     * @return オプションを持つ場�?true、それ以外�?�false�?
     */
    public boolean hasOption(String name) {
        if(name == null || name.length() <= 0) return false;
        boolean hasOption = optionValues.containsKey(name);
        if(!hasOption) {
            for(String optionAndValue : optionAndValues) {
                if(optionAndValue.startsWith(name)) {
                    hasOption = true;
                    break;
                }
            }
        }
        return hasOption;
    }

    /**
     * �?定された名前で�?初に見つかったオプションの値を取得します�??
     * @param optionName オプションの名前
     * @return オプションの値。指定された名前のオプションが存在しな�?か�?��?�を持たな�?場合�?�null�?
     */
    public String getOptionValue(String optionName) {
        if(optionName == null || optionName.length() <= 0) return null;
        String value = optionValues.get(optionName);
        if(value == null) {
            for(String optionAndValue : optionAndValues) {
                if(optionAndValue.startsWith(optionName)
                        && !optionAndValue.equals(optionName)) {
                    value = optionAndValue.substring(optionName.length());
                    break;
                }
            }
        }
        return value;
    }

    /**
     * オプションの値とはなり得な�?、すべての単独の値を取得します�??
     * @return 出現�?に値が�?�納された�?字�?��?��?��?�単独の値が存在しな�?場合�?�長�?0の配�?��??
     */
    public String[] getValues() {
        String[] values = new String[singleValues.size()];
        singleValues.toArray(values);
        return values;
    }

}
