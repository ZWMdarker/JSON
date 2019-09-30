package co.zhang.json;





import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import javax.naming.event.ObjectChangeListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestJson {


    @Test
    public void getJson(){
        //读取文本文件
        String fileName ="F://data/test.txt";
        String [] infos = toArrayByFileReader(fileName);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("test","");
        map.put("sub",new ArrayList<Object>());
        boolean flag = true;
        int lastLevel = 0;
        Map<String, Object> lastMap = null;
        Map<String, Object> currentMap;
        Map<Integer,Map<String,Object>> flagMap = new HashMap<Integer, Map<String, Object>>();
        flagMap.put(0,map);
        for (String info: infos){
            if (info != null && !info.equals("")) {
                int infoLevel = getInfoLevel(info);
                if (infoLevel == lastLevel) {
                    currentMap= new HashMap<String, Object>();
                    currentMap.put("text", getRealInfo(info, infoLevel));
                    currentMap.put("sub", new ArrayList<Object>());
                    Map<String, Object> fatherMap = flagMap.get(infoLevel-1);
                    List<Object> subList = (List<Object>)fatherMap.get("sub");
                    subList.add(currentMap);
                    lastMap = currentMap;
                    lastLevel = infoLevel;
                    flagMap.put(infoLevel, currentMap);
                } else if (infoLevel > lastLevel) {
                    if (flag){
                        lastMap = map;
                        flag = false;
                    }
                    currentMap = new HashMap<String, Object>();
                    currentMap.put("text", getRealInfo(info, infoLevel));
                    currentMap.put("sub", new ArrayList<Object>());
                    List<Object> subList = (List<Object>)lastMap.get("sub");
                    subList.add(currentMap);
                    lastMap.put("sub", subList);
                    lastMap = currentMap;
                    lastLevel = infoLevel;
                    flagMap.put(infoLevel, currentMap);
                } else {
                    //获取其父级Map
                    Map<String, Object> fatherMap = flagMap.get(infoLevel-1);
                    currentMap = new HashMap<String, Object>();
                    currentMap.put("text", getRealInfo(info, infoLevel));
                    currentMap.put("sub", new ArrayList<Object>());
                    List<Object> subList = (List<Object>)fatherMap.get("sub");
                    subList.add(currentMap);
                    fatherMap.put("sub", subList);
                    lastMap = currentMap;
                    lastLevel= infoLevel;
                    flagMap.put(infoLevel, currentMap);
                }
            }
        }
        String result = JSONObject.toJSONString(map.get("sub"));
        FileWriter writer;
        try {
            writer = new FileWriter("F://data/result.txt");
            writer.write(result);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        return result;
    }

    /**
     * 取出标识，获取实际存储的值
     */
    public String getRealInfo(String info, int infoLevel){
        String result = null;
        if (infoLevel==1){
            result = info.substring(info.indexOf(" ")+1);
        }else if (infoLevel == 3 || infoLevel ==2){
            result = info.substring(info.indexOf("、")+1);
        }else if (infoLevel == 4){
            result = info.substring(info.indexOf("）")+1);
        }
        return result;
    }

    /**
     * 获取该行文件所属等级
     */
    public int getInfoLevel(String info){
        String secondLevel = "^[\\u4e00-\\u9fa5]{1,3}、.*$";
        String thiredLevel = "^[0-9]{1,3}、.*$";
        String firstLevel = "^第.*部分.*$";
        if (Pattern.compile(firstLevel).matcher(info).matches()){
            return 1;
        }else if (Pattern.compile(secondLevel).matcher(info).matches()){
            return 2;
        }else if (Pattern.compile(thiredLevel).matcher(info).matches()){
            return 3;
        }else {
            return 4;
        }
    }

    private String[] toArrayByFileReader(String pathName) {
        // 使用ArrayList来存储每行读取到的字符串
        ArrayList<String> arrayList = new ArrayList<String>();
        try {
            File file = new File(pathName);
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file),"gbk");
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                arrayList.add(str);
            }
            bf.close();
            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对ArrayList中存储的字符串进行处理
        int length = arrayList.size();
        String[] array = new String[length];
        for (int i = 0; i < length; i++) {
            String s = arrayList.get(i);
            array[i] = s;
        }

        // 返回数组
        return array;
    }
}
