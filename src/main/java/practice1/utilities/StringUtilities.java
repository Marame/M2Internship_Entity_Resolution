package practice1.utilities;

/**
 * Created by romdhane on 10/08/17.
 */
public class StringUtilities {
    public boolean hasOneToken(String str){
        String[]  tokens =  str.split("\\s+");
        if(tokens.length>1) return false;
        else
            return true;

    }

    public String getAcronym(String str){
        String newString = "" ;
        Character c1, c2;

        c1 = new Character('[');

        String[]  tokens =  str.split("\\s+");
        for (String s : tokens){
            c2 = new Character(s.charAt(0));
            if(c1.compareTo(c2)!=0)
            if (Character.isUpperCase(c2))
                newString += c2;
        }

        return newString;
    }

}
