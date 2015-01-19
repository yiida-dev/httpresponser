package yiida.util;

/**
 * オプション文字列のロング名を解釈する、Argumentsをラップするクラスです。
 * オプションのロング名とは"--"に続く文字列をオプションの名前とする形式です。
 * オプションと値の形式は厳密なオプション形式として扱われます。
 * また、オプションとその値が"="で結合された形式も認めます。
 * この場合は"="はオプションにも値にも含まれません。
 * @author yiida
 *
 */
public class LongNameArgs {

    private final Arguments args;
    
    /**
     * ラップするArgumentsオブジェクトを指定してオブジェクトを初期化します。
     * @param args Argumentsオブジェクト
     */
    public LongNameArgs(Arguments args) {
        this.args = args;
    }
    
    /**
     * この引数が指定された名前(先頭の"--"を含めない)のオプションを持つかどうかを取得します。
     * @param name オプションの名前
     * @return オプションを持つ場合true、それ以外はfalse。
     */
    public boolean hasOption(String name) {
        boolean hasOption = args.hasOptionStrictly("-" + name);
        if(!hasOption) hasOption = args.hasOption("-" + name + "=");
        return hasOption;
    }
    
    /**
     * 指定された名前(先頭の"--"を含めない)で最初に見つかったオプションの値を取得します。
     * @param name オプションの名前
     * @return オプションの値。指定された名前のオプションが存在しないか、値を持たない場合はnull。
     */
    public String getOptionValue(String name) {
        String value = args.getOptionValueStrictly("-" + name);
        if(value == null) value = args.getOptionValue("-" + name + "=");
        if(value != null && args.hasOptionStrictly("-" + name + "=")) value = null;
        return value;
    }
}
