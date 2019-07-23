package com.chongdong.messagecenter.utils;

import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class UidGenerator {


    //最小步数
    private int min_step_num = 0;

    //最大步数
    private int max_step_num = 999999;

    //当前步数
    private int current_step = 1;

    //步长
    private int step = 1;

    //运行服务器特征值（每台不同）
    private String group;

    //默认step长度
    private int step_length = 6;


    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private Date date = new Date();

    private long last_id_time;

    private static Map<String, UidGenerator> map = new HashMap<String, UidGenerator>();


    public synchronized  static UidGenerator getInstance(String group){
        UidGenerator generator = map.get(group);
        if(ObjectUtils.isEmpty(generator)){
            UidGenerator _generator = new UidGenerator(group);
            map.put(group, _generator);
            return _generator;
        }else {
            return generator;
        }

    }

    /**
     * 构造方法
     *
     * @param group
     */
    private UidGenerator(String group) {
        this.last_id_time = System.currentTimeMillis();
        this.group = group;
    }


    /**
     * E------2019-----04--14--23--40--30--081---000007
     * GROUP--YYYY-----MM--DD--HH--mm--ss--SSS---000000
     */


    /**
     * 获取系统唯一Uid
     * @return
     */
    public synchronized String nextUid() {

            long time = System.currentTimeMillis();
            this.date.setTime(time);
            StringBuffer sb = new StringBuffer();
            sb.append(this.group);
            sb.append(this.format.format(this.date));
            sb = nextStep(time, sb);
            return sb.toString();

    }


    /**
     * 获取下个步长
     * @param time
     * @param sb
     * @return
     */
    public synchronized StringBuffer nextStep(long time, StringBuffer sb) {

        if (time == this.last_id_time) {
            this.current_step++;
            //当步前进到最大  阻塞到下一毫秒
            if (this.current_step > this.max_step_num) {
                return nextStep(time, sb);
            }
        }else {
            this.current_step = this.min_step_num ++;
        }

        this.last_id_time = time;
        return formatStep(current_step, step_length, sb);
    }

    /**
     * 构造length长度的字符串 前缀用"0"补全
     * @param step
     * @param length
     * @param _step
     * @return
     */
    private StringBuffer formatStep(int step, int length, StringBuffer _step) {
        int step_length = String.valueOf(step).length();
        for (int i = 0; i < (length - step_length); i++) {
            _step.append(0);
        }
        _step.append(step);
        return _step;
    }

    public static void main(String[] args) {
        UidGenerator uid = new UidGenerator("E");
        //List<String> ids = new ArrayList<>();
        int num = 1000000;
        int count = 0;
        long begin_time = System.currentTimeMillis();
        for (int i = 0; i < num; i ++){
            String id = uid.nextUid();
            //System.out.println(id);
//            if(ids.contains(id)){
//                count ++;
//            }else {
//                ids.add(id);
//            }
        }
        long end_time = System.currentTimeMillis();

        System.out.println("花费时间（ms）:" + (end_time - begin_time));
        //System.out.println("生成ID数：" + ids.size());
        System.out.println("重复ID数：" + count);
        System.out.println("QPS:" + num * 1000 /(end_time - begin_time) );

        System.out.println("QPMS:" + num  /(end_time - begin_time) );

    }



}
