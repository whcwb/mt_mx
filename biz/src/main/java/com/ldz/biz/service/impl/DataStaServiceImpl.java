package com.ldz.biz.service.impl;

import com.ldz.biz.model.StudentAllModel;
import com.ldz.biz.model.TraineeInformation;
import com.ldz.biz.service.DataStaService;
import com.ldz.biz.service.TraineeInformationService;
import com.ldz.sys.model.SysJg;
import com.ldz.sys.service.JgService;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.bean.SimpleCondition;
import com.ldz.util.commonUtil.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataStaServiceImpl implements DataStaService {

    @Autowired
    private JgService jgService;

    @Autowired
    private TraineeInformationService informationService;



    @Override
    public ApiResponse<List<StudentAllModel>> getAllStudentCount(String startTime, String endTime, String jgdm, String[] lx) throws ParseException {


        if (StringUtils.isBlank(startTime)) {
            startTime = DateUtils.getDateStr(new Date(), "yyyy") + " 00:00:00";
        } else {
            startTime += " 00:00:00";
        }
        if (StringUtils.isBlank(endTime)) {
            endTime = DateUtils.getDateStr(new Date(), "yyyy") + " 23:59:59";
        } else {
            endTime += " 23:59:59";
        }

        List<String> monthBetween = DateUtils.getMonthBetween(startTime.substring(0, 4) + "-01", endTime.substring(0, 4) + "-12");
        List<StudentAllModel> models = new ArrayList<>();

        List<SysJg> all = jgService.findAll();
        Map<String, String> jgMap = all.stream().collect(Collectors.toMap(SysJg::getJgdm, SysJg::getJgmc));

        SimpleCondition condition = new SimpleCondition(TraineeInformation.class);
        condition.gte(TraineeInformation.InnerColumn.registrationTime, startTime);
        condition.lte(TraineeInformation.InnerColumn.registrationTime, endTime);
        if (StringUtils.isNotBlank(jgdm)) {
            condition.eq(TraineeInformation.InnerColumn.jgdm, jgdm);
        }
        if (lx != null && lx.length > 0) {
            SimpleCondition simpleCondition = new SimpleCondition(SysJg.class);
            simpleCondition.in(SysJg.InnerColumn.lx, Arrays.asList(lx));
            List<SysJg> jgs = jgService.findByCondition(simpleCondition);
            if (CollectionUtils.isNotEmpty(jgs)) {
                List<String> jgdms = jgs.stream().map(SysJg::getJgdm).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(jgdms)) {
                    condition.in(TraineeInformation.InnerColumn.jgdm, jgdms);
                }
            }
        }
        condition.setOrderByClause("  jgdm asc ");
        List<TraineeInformation> informationList = informationService.findByCondition(condition);
        Map<String, List<TraineeInformation>> collect = informationList.stream().collect(Collectors.groupingBy(TraineeInformation::getJgdm, TreeMap::new, Collectors.toList()));
        Set<String> set = collect.keySet();
        HashMap<String, Integer> map = new LinkedHashMap<>();
        for (String s : set) {
            StudentAllModel allModel = new StudentAllModel();
            List<StudentAllModel.StuAll> stuAlls = new ArrayList<>();
            int dropOut = 0;

            List<TraineeInformation> traineeInformatics = collect.get(s);

            for (int j = 0; j < monthBetween.size(); j++) {
                int test = 0;
                String s1 = monthBetween.get(j);
                int total = 0;
                if (map.containsKey(s1)) {
                    test = map.get(s1);
                } else {
                    map.put(s1, 0);
                }


                for (int i = traineeInformatics.size() - 1; i >= 0; i--) {
                    TraineeInformation traineeInformation = traineeInformatics.get(i);
                    if (StringUtils.startsWith(traineeInformation.getRegistrationTime(), s1)) {

                        if (StringUtils.equals(traineeInformation.getStatus(), "60")) {
                            dropOut++;
                        }
                        test++;
                        total++;
                        traineeInformatics.remove(i);
                    }
                }
                StudentAllModel.StuAll stuAll = new StudentAllModel.StuAll();
                stuAll.setTime(s1);
                stuAll.setCount(total);
                stuAlls.add(stuAll);
                if (j == monthBetween.size() - 1) {
                    int to = 0;

                    for (StudentAllModel.StuAll a : stuAlls) {
                        to += a.getCount();
                    }

                    allModel.setAll(to);
                    allModel.setDropOut(dropOut);
                    allModel.setRealAll(to - dropOut);

                }
                allModel.setStu(stuAlls);
                allModel.setJgmc(jgMap.get(s));
                map.put(s1, test);


            }

            models.add(allModel);
        }
        if (CollectionUtils.isNotEmpty(models)) {
            StudentAllModel model = new StudentAllModel();
            List<StudentAllModel.StuAll> stuAlls = new ArrayList<>();
            model.setJgmc("合计");
            for (String s : map.keySet()) {

                StudentAllModel.StuAll stuAll = new StudentAllModel.StuAll();
                stuAll.setTime(s);
                stuAll.setCount(map.get(s));
                stuAlls.add(stuAll);
                model.setStu(stuAlls);
            }
            long asInt = models.stream().mapToLong(StudentAllModel::getAll).reduce(Long::sum).orElse(0);
            long asInt1 = models.stream().mapToLong(StudentAllModel::getDropOut).reduce(Long::sum).orElse(0);
            long asInt2 = models.stream().mapToLong(StudentAllModel::getRealAll).reduce(Long::sum).orElse(0);
            model.setAll(asInt);
            model.setDropOut(asInt1);
            model.setRealAll(asInt2);
            models.add(model);
        }
        return ApiResponse.success(models);
    }

    @Override
    public ApiResponse<List<StudentAllModel>> getStudentCount(String startTime, String endTime, String jgdm, String[] lx) {
        if (StringUtils.isBlank(startTime)) {
            startTime = DateUtils.getDateStr(new Date(), "yyyy") + "-01-01 00:00:00";
        } else {
            startTime += " 00:00:00";
        }
        if (StringUtils.isBlank(endTime)) {
            endTime = DateUtils.getDateStr(new Date(), "yyyy") + "-12-31 23:59:59";
        } else {
            endTime += " 23:59:59";
        }

        List<StudentAllModel> models = new ArrayList<>();

        List<SysJg> all = jgService.findAll();
        Map<String, String> jgMap = all.stream().collect(Collectors.toMap(SysJg::getJgdm, SysJg::getJgmc));

        SimpleCondition condition = new SimpleCondition(TraineeInformation.class);
        condition.gte(TraineeInformation.InnerColumn.registrationTime, startTime);
        condition.lte(TraineeInformation.InnerColumn.registrationTime, endTime);
        if (StringUtils.isNotBlank(jgdm)) {
            condition.eq(TraineeInformation.InnerColumn.jgdm, jgdm);
        }
        if (lx != null && lx.length > 0) {
            SimpleCondition simpleCondition = new SimpleCondition(SysJg.class);
            simpleCondition.in(SysJg.InnerColumn.lx, Arrays.asList(lx));
            List<SysJg> jgs = jgService.findByCondition(simpleCondition);
            if (CollectionUtils.isNotEmpty(jgs)) {
                List<String> jgdms = jgs.stream().map(SysJg::getJgdm).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(jgdms)) {
                    condition.in(TraineeInformation.InnerColumn.jgdm, jgdms);
                }
            }
        }
        condition.setOrderByClause("  jgdm asc ");
        List<TraineeInformation> informationList = informationService.findByCondition(condition);
        Map<String, List<TraineeInformation>> collect = informationList.stream().collect(Collectors.groupingBy(TraineeInformation::getJgdm, TreeMap::new, Collectors.toList()));
        Set<String> set = collect.keySet();
        for (String s : set) {
            List<TraineeInformation> traineeInformatics = collect.get(s);
            Map<String, Long> count1 = traineeInformatics.stream().collect(Collectors.groupingBy(TraineeInformation::getCarType, Collectors.counting()));
            StudentAllModel allModel = new StudentAllModel();
            long count = traineeInformatics.size();
            allModel.setAll(count);
            Long a1 = count1.get("A1");
            Long a2 = count1.get("A2");
            Long a3 = count1.get("A3");
            Long b2 = count1.get("B2");
            Long c1 = count1.get("C1");
            Long c2 = count1.get("C2");

            allModel.setJgmc(jgMap.get(s));
            allModel.setA1(a1 == null ? 0L : a1);
            allModel.setA2(a2 == null ? 0L : a2);
            allModel.setA3(a3 == null ? 0L : a3);
            allModel.setB2(b2 == null ? 0L : b2);
            allModel.setC1(c1 == null ? 0L : c1);
            allModel.setC2(c2 == null ? 0L : c2);


            models.add(allModel);
        }
        if (CollectionUtils.isNotEmpty(models)) {
            StudentAllModel allModel = new StudentAllModel();
            allModel.setJgmc("合计");
            allModel.setA1(models.stream().mapToLong(StudentAllModel::getA1).reduce(Long::sum).orElse(0));
            allModel.setA2(models.stream().mapToLong(StudentAllModel::getA2).reduce(Long::sum).orElse(0));
            allModel.setA3(models.stream().mapToLong(StudentAllModel::getA3).reduce(Long::sum).orElse(0));
            allModel.setB2(models.stream().mapToLong(StudentAllModel::getB2).reduce(Long::sum).orElse(0));
            allModel.setC1(models.stream().mapToLong(StudentAllModel::getC1).reduce(Long::sum).orElse(0));
            allModel.setC2(models.stream().mapToLong(StudentAllModel::getC2).reduce(Long::sum).orElse(0));
            allModel.setAll(models.stream().mapToLong(StudentAllModel::getAll).reduce(Long::sum).orElse(0));
            models.add(allModel);
        }
        return ApiResponse.success(models);
    }

    private static double formatValue(double val) {

        BigDecimal bg = new BigDecimal(val).setScale(2, RoundingMode.UP);
        return bg.doubleValue();

    }


}
