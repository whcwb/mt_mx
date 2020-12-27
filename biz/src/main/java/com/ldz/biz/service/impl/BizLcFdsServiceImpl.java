package com.ldz.biz.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.ldz.biz.mapper.BizLcFdsMapper;
import com.ldz.biz.model.BizLcFd;
import com.ldz.biz.model.BizLcFds;
import com.ldz.biz.model.BizLcJl;
import com.ldz.biz.model.BizLcWxjl;
import com.ldz.biz.service.BizLcFdService;
import com.ldz.biz.service.BizLcFdsService;
import com.ldz.biz.service.BizLcJlService;
import com.ldz.biz.service.BizLcWxjlService;
import com.ldz.sys.base.BaseServiceImpl;
import com.ldz.sys.base.LimitedCondition;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.bean.SimpleCondition;
import com.ldz.util.exception.RuntimeCheck;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BizLcFdsServiceImpl extends BaseServiceImpl<BizLcFds, String> implements BizLcFdsService {

    @Autowired
    private BizLcFdsMapper baseMapper;
    @Autowired
    private BizLcFdService fdService;
    @Autowired
    private BizLcWxjlService wxjlService;
    @Autowired
    private BizLcJlService lcJlService;

    @Override
    protected Mapper<BizLcFds> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public boolean fillPagerCondition(LimitedCondition condition) {
        String jlLx = getRequestParamterAsString("jlLx");
        if (StringUtils.isNotBlank(jlLx)) {
            List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.jlLx, jlLx);
            if(CollectionUtils.isEmpty(wxjls)){
                return false;
            }else {
                List<String> list = wxjls.stream().map(BizLcWxjl::getId).collect(Collectors.toList());
                condition.in(BizLcFd.InnerColumn.jlId, list);
            }
        }
        return true;
    }

    @Override
    protected void afterPager(PageInfo<BizLcFds> resultPage) {
        List<BizLcFds> list = resultPage.getList();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(bizLcFds -> {
            String lcId = bizLcFds.getLcId();
            List<BizLcFd> fds = fdService.findByIds(Arrays.asList(lcId.split(",")));
            bizLcFds.setFds(fds);
        });
    }

    @Override
    public ApiResponse<String> getPager(Page<BizLcFds> pager) {
        LimitedCondition condition = getQueryCondition();
        String jlLx = getRequestParamterAsString("jlLx");
        ApiResponse<String> res = new ApiResponse<>();
        if(StringUtils.isNotBlank(jlLx)){
            List<BizLcWxjl> wxjls = wxjlService.findEq(BizLcWxjl.InnerColumn.jlLx, jlLx);
            if(CollectionUtils.isEmpty(wxjls)){
                res.setResult("0");
                return res;
            }else {
                List<String> list = wxjls.stream().map(BizLcWxjl::getId).collect(Collectors.toList());
                condition.in(BizLcFd.InnerColumn.jlId, list);
            }
        }
        PageInfo<BizLcFds> pageInfo = findPage(pager, condition);
        List<BizLcFds> list = pageInfo.getList();
        List<String> lcids = new ArrayList<>();
         list.stream().map(BizLcFds::getLcId).forEach(s -> {
             String[] split = s.split(",");
             lcids.addAll(Arrays.asList(split));
         });
         if(CollectionUtils.isEmpty(lcids)) {
             res.setPage(new PageInfo());
             return res;
         }
        List<BizLcFd> lcFds = fdService.findByIds(lcids);

        List<String> jlids = new ArrayList<>();
        lcFds.stream().map(BizLcFd::getLcId).forEach(s -> {
            String[] split = s.split(",");
            jlids.addAll(Arrays.asList(split));
        });
        List<BizLcJl> jls = lcJlService.findByIds(jlids);
        list.forEach(bizLcFds -> {
            String lcId = bizLcFds.getLcId();
            List<BizLcFd> fds = lcFds.stream().filter(lcFd ->
                    Arrays.asList(lcId.split(",")).contains(lcFd.getId())
            ).collect(Collectors.toList());
            bizLcFds.setFds(fds);

            fds.stream().forEach(lcFd -> {
                List<BizLcJl> jlList = jls.stream().filter(bizLcJl -> Arrays.asList(lcFd.getLcId().split(",")).contains(bizLcJl.getId())).collect(Collectors.toList());
                lcFd.setJlList(jlList);
            });
        });
        List<BizLcFds> fds = findByCondition(condition);
        int sum = fds.stream().mapToInt(BizLcFds::getFdje).sum();
        res.setResult("" + sum);
        res.setPage(pageInfo);
        return res;
    }

    @Override
    public void downloadExcel(Page<BizLcFds> pager, HttpServletRequest request, HttpServletResponse response) throws IOException, WriteException {
        ApiResponse<String> apiResponse = getPager(pager);
        String lcKm = getRequestParamterAsString("lcKm");
        RuntimeCheck.ifBlank(lcKm, "请选择导出科目");
        Map<String, String> map = new HashMap<>();
        map.put("2", "科目二");
        map.put("3", "科目三");
        List<BizLcFds> list = apiResponse.getPage().getList();
        String range = getRequestParamterAsString("cjsjInRange");
        List<String> times = Arrays.asList(range.split(","));
        String start = times.get(0).substring(0, 10).replace("-", "年").replace("-", "月") + "日";
        String end = times.get(1).substring(0, 10).replace("-", "年").replace("-", "月") + "日";
        String fileName = "返现登记表";
        response.setContentType("application/msexcel");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("pragma", "no-cache");
        response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1") + ".xls");
        OutputStream out = response.getOutputStream();
        WritableWorkbook workbook = Workbook.createWorkbook(out);
        WritableSheet sheet = workbook.createSheet("返现登记", 0);
        sheet.mergeCells(0, 0, 7, 0);

        WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 16, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        WritableCellFormat titleFormat = new WritableCellFormat(titleFont);
        titleFormat.setAlignment(jxl.format.Alignment.CENTRE);

        CellView view = new CellView();
        view.setSize(5000);
        for (int i = 0; i < 8; i++) {
            sheet.setColumnView(i, view);
        }
        sheet.addCell(new Label(0, 0, "知音考场返现登记表(" + map.get(lcKm) + ")", titleFormat));

        WritableFont font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        WritableCellFormat format = new WritableCellFormat(font);
        format.setAlignment(jxl.format.Alignment.RIGHT);
        sheet.mergeCells(0, 1, 7, 1);
        sheet.addCell(new Label(0, 1, "返现时间：" + start + "至" + end, format));

        WritableFont contentFont = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        WritableCellFormat contentFormat = new WritableCellFormat(contentFont);
        contentFormat.setAlignment(Alignment.CENTRE);

        sheet.addCell(new Label(0, 2, "序号", contentFormat));
        sheet.addCell(new Label(1, 2, "返现日期", contentFormat));
        sheet.addCell(new Label(2, 2, "凭证号", contentFormat));
        sheet.addCell(new Label(3, 2, "驾校", contentFormat));
        sheet.addCell(new Label(4, 2, "教练员", contentFormat));
        sheet.addCell(new Label(5, 2, "练车日期", contentFormat));
        sheet.addCell(new Label(6, 2, "练车金额", contentFormat));
        sheet.addCell(new Label(7, 2, "返点金额", contentFormat));

        sheet.addCell(new Label(0, list.size() + 3, "合计", contentFormat));
        sheet.mergeCells(1, list.size() + 3, 5, list.size() + 3);
//        sheet.mergeCells(6, list.size() + 3, 7, list.size() + 3);
        if (CollectionUtils.isNotEmpty(list)) {
            Set<String> jlids = list.stream().map(BizLcFds::getJlId).collect(Collectors.toSet());
            List<BizLcWxjl> wxjls = wxjlService.findByIds(jlids);
            Map<String, BizLcWxjl> jxmcMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(wxjls)) {
                jxmcMap = wxjls.stream().collect(Collectors.toMap(BizLcWxjl::getId, p -> p));
            }
            for (int i = 0; i < list.size(); i++) {
                BizLcFds fd = list.get(i);
                BizLcWxjl wxjl = jxmcMap.get(fd.getJlId());
                String lcId = fd.getLcId().split(",")[0];
                SimpleCondition condition = new SimpleCondition(BizLcJl.class);
                condition.and().andCondition(" pz = '" + lcId + "' or id = '" + lcId + "'");
                BizLcJl jl = lcJlService.findByCondition(condition).get(0);
                sheet.addCell(new Label(0, i + 3, i + 1 + "", contentFormat));
                sheet.addCell(new Label(1, i + 3, fd.getCjsj().substring(0, 10), contentFormat));
                sheet.addCell(new Label(2, i + 3, fd.getId()));
                sheet.addCell(new Label(3, i + 3, wxjl.getJlJx(), contentFormat));
                sheet.addCell(new Label(4, i + 3, wxjl.getJlXm(), contentFormat));
                sheet.addCell(new Label(5, i + 3, jl.getCjsj().substring(0, 10), contentFormat));
                sheet.addCell(new Label(6, i + 3, fd.getLcFy() + "", contentFormat));
                sheet.addCell(new Label(7, i + 3, fd.getFdje() + "", contentFormat));
            }
            sheet.addCell(new Label(6, list.size() + 3, list.stream().mapToInt(BizLcFds::getLcFy).sum() + "", contentFormat));
            sheet.addCell(new Label(7, list.size() + 3, list.stream().mapToInt(BizLcFds::getFdje).sum() + "", contentFormat));
        }
        workbook.write();
        workbook.close();
    }

}
