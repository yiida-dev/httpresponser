package yiida.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * "オプション"と"値"の形式を持つ引数を表すクラスです。
 * アプリケーションのmainエントリポイントに渡されるString配列を解析すると想定されています。
 * このクラスはString配列を入力として、以下のルールに従って解析します。<br/>
 * <br/>
 * 一つの文字列を"オプション"または"値"または"オプション"+"値"であると解釈します。
 * オプションは"-"で始まり、それに続く1文字以上の名前を持ちます。
 * オプションではない文字列とオプション以外の部分文字列はすべて値となります。
 * オプションは値を持つことができ、オプションに続く値をそのオプションの値と認識します。
 * 続く値、とは同じ文字列内のオプションの次の値もしくは次の文字列が値であった場合はその値です。
 * 続く値が無い場合はオプションは値を持ちません。
 * これはオプションの名前の解釈の仕方により、同じ文字列でも複数の"オプション"と"値"になることを意味します。
 * たとえば、"-abc"という文字列とその次に"xyz"という文字列があった場合、
 * "-a"オプションの値は"bc"、
 * "-ab"オプションの値は"c"、
 * "-abc"オプションの値は"xyz"となります。<br/>
 * <br/>
 * 厳密なオプション形式について：
 * このクラスはオプションの厳密な形式を扱うことができます。
 * 厳密な形式では、オプションと値はそれぞれ別の文字列として扱われます。
 * 一つの文字列が複数のオプションと値の組み合わせに解釈されることはありません。
 * 
 * @author yiida
 *
 */
public final class Arguments {

    private List<String> optionAndValues;
    private Map<String, String> optionValues;
    private List<String> singleValues;
    private List<String> certainValues;
    
    /**
     * 引数として解釈する文字列配列を指定してオブジェクトを初期化します。
     * @param args 文字列配列
     */
    public Arguments(String[] args) {
        optionAndValues = new ArrayList<String>();
        optionValues = new HashMap<String, String>();
        singleValues = new ArrayList<String>();
        certainValues = new ArrayList<String>();
        
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
                certainValues.add(args[i]);
            }
        }
    }
    
    /**
     * この引数が指定された名前(先頭の"-"を含めない)のオプションを持つかどうかを取得します。
     * @param name オプションの名前
     * @return オプションを持つ場合true、それ以外はfalse。
     */
    public boolean hasOption(String name) {
        if(name == null || name.length() <= 0) return false;
        boolean hasOption = optionValues.containsKey(name);
        if(!hasOption) {
            for(String optionAndValue: optionAndValues) {
                if(optionAndValue.startsWith(name)) {
                    hasOption = true;
                    break;
                }
            }
        }
        return hasOption;
    }
    
    /**
     * 指定された名前(先頭の"-"を含めない)で最初に見つかったオプションの値を取得します。
     * @param optionName オプションの名前
     * @return オプションの値。指定された名前のオプションが存在しないか、値を持たない場合はnull。
     */
    public String getOptionValue(String optionName) {
        if(optionName == null || optionName.length() <= 0) return null;
        String value = optionValues.get(optionName);
        if(value == null) {
            for(String optionAndValue: optionAndValues) {
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
     * 厳密なオプション形式に基づいて、
     * 指定された名前(先頭の"-"を含めない)のオプションを持つかどうかを取得します。
     * @param name オプションの名前
     * @return オプションを持つ場合true、それ以外はfalse。
     */
    public boolean hasOptionStrictly(String name) {
        if(name == null || name.length() <= 0) return false;
        return optionAndValues.contains(name);
    }
    
    /**
     * 厳密なオプション形式に基づいて、
     * 指定された名前(先頭の"-"を含めない)で最初に見つかったオプションの値を取得します。
     * @param optionName オプションの名前
     * @return オプションの値。指定された名前のオプションが存在しないか、値を持たない場合はnull。
     */
    public String getOptionValueStrictly(String optionName) {
        if(optionName == null || optionName.length() <= 0) return null;
        return optionValues.get(optionName);
    }
    
    /**
     * オプションの値とはなりえない、すべての単独の値を取得します。
     * @return 出現順に値が格納された文字列配列。単独の値が存在しない場合は長さ0の配列。
     */
    public String[] getValues() {
        String[] values = new String[singleValues.size()];
        singleValues.toArray(values);
        return values;
    }
    
    /**
     * 厳密なオプション形式に基づいて、
     * オプションの値と単独の値の両方を含むすべての値を取得します。
     * @return 出現順に値が格納された文字列配列。該当する値が存在しない場合は長さ0の配列。
     */
    public String[] getCertainValues() {
        String[] values = new String[certainValues.size()];
        certainValues.toArray(values);
        return values;
    }
}
