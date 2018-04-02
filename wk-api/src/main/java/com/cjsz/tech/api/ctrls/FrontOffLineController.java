package com.cjsz.tech.api.ctrls;

import com.cjsz.tech.api.APIConstants;
import com.cjsz.tech.api.service.FrontOffLineService;
import com.cjsz.tech.book.domain.BookIndexRecord;
import com.cjsz.tech.book.service.BookIndexRecordService;
import com.cjsz.tech.dev.domain.Device;
import com.cjsz.tech.dev.service.DeviceService;
import com.cjsz.tech.system.beans.Constants;
import com.cjsz.tech.system.utils.LogUtil;
import com.cjsz.tech.utils.JsonResult;
import com.github.pagehelper.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 离线服务控制器
 * Created by shiaihua on 16/12/22.
 */
@Controller
public class FrontOffLineController {

    @Autowired
    FrontOffLineService frontOffLineService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    BookIndexRecordService bookIndexRecordService;


    /**
     * 获取需要同步的数据数量
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/offline/dataCount", method = {RequestMethod.POST})
    @ResponseBody
    public Object getDataCount(HttpServletRequest request, @RequestParam("token") String token) {
        if (StringUtil.isEmpty(token)) {
            return JsonResult.getError(APIConstants.TOKEN_NULL);
        }
        String timevstr = request.getParameter("timev");
        if (StringUtils.isEmpty(timevstr)){
            return JsonResult.getError(APIConstants.OFFLINE_ERROR);
        }
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if ("0".equals(timevstr)){
            timevstr = "1970-01-01 00:00:00";
        }else {
            Long time = Long.parseLong(timevstr);
            timevstr = format.format(time);

        }
        String module = request.getParameter("mod");
        if (!frontOffLineService.isContainsModule(module)) {
            return JsonResult.getError(APIConstants.OFFLINE_NODULE);
        }
        String deviceCode = request.getParameter("devcode");
        //检查设备号
        if (StringUtils.isEmpty(deviceCode)) {
            return JsonResult.getError(APIConstants.DEVICE_INVALID);
        }
        Device deviceObj = deviceService.getDeviceByCode(deviceCode);
        if (deviceObj == null) {
            return JsonResult.getError(APIConstants.NO_DEVICE);
        }
        if (deviceObj.getEnabled() != 1) {
            return JsonResult.getError(APIConstants.DEVICE_DISABLED);
        }

        Long orgid = deviceObj.getOrg_id();
        List<Map<String, Object>> result = frontOffLineService.getOffLineDataCount(module, orgid, timevstr, deviceObj.getDevice_id());

        //接受内存参数memory
        String memory = request.getParameter("memory");
        String version = request.getParameter("version");
        //设备更新离线状态和内存、同步数据时间
        if (StringUtils.isNotEmpty(memory)) {
            deviceService.updeatOfflineParam(memory, deviceCode, version);
        }
        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.OFFLINE_SUCCESS);
        jsonResult.setData(result);
        return jsonResult;
    }


    /**
     * 获取离线数据
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/offline/data", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfList(HttpServletRequest request, @RequestParam("token") String token) {

        //TOKEN
        if (StringUtil.isEmpty(token)) {
            return JsonResult.getError(APIConstants.TOKEN_NULL);
        }
        String timevstr = request.getParameter("timev");
        if (StringUtils.isEmpty(timevstr)){
            return JsonResult.getError(APIConstants.OFFLINE_ERROR);
        }
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long currentTime = 0L;
        if ("0".equals(timevstr)){
            timevstr = "1970-01-01 00:00:00";
        }else {
            Long time = Long.parseLong(timevstr);
            currentTime = time;
            timevstr = format.format(time);
        }

        String module = request.getParameter("mod");
        if (!frontOffLineService.isContainsModule(module)) {
            LogUtil.error("no such module:"+module);
            return JsonResult.getError(APIConstants.OFFLINE_NODULE);
        }

        String deviceCode = request.getParameter("devcode");
        //检查设备号
        if (StringUtils.isEmpty(deviceCode)) {
            return JsonResult.getError(APIConstants.DEVICE_INVALID);
        }
        Device deviceObj = deviceService.getDeviceByCode(deviceCode);
        if (deviceObj == null) {
            return JsonResult.getError(APIConstants.NO_DEVICE);
        }
        if (deviceObj.getEnabled() != 1) {
            return JsonResult.getError(APIConstants.DEVICE_DISABLED);
        }

        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            basePath = basePath + ":" + request.getServerPort();
        }
        basePath += path;

        Long orgid = deviceObj.getOrg_id();
        String pageNum = request.getParameter("pageNum");
        String pageSize = request.getParameter("pageSize");
        Integer num;
        Integer size;
        if (StringUtils.isEmpty(pageNum)) {
            num = 1;
        } else {
            try {
                num = Integer.parseInt(pageNum);
            } catch (Exception e) {
                return JsonResult.getError(APIConstants.PAGENUM_ERROR);
            }
        }

        if (StringUtils.isEmpty(pageSize)) {
            size = 20000;
        } else {
            try {
                size = Integer.parseInt(pageSize);
            } catch (Exception e) {
                return JsonResult.getError(APIConstants.PAGESIZE_ERROR);
            }
        }
        TreeMap<String, Object> dataMap = new TreeMap<>();
        List<String> codes = deviceService.getSyncDeviceCodes();
        if (codes != null && codes.size() > 0 && codes.contains(deviceCode)) {
            dataMap = (TreeMap<String, Object>) frontOffLineService.getOffLineContent(basePath, module, orgid, timevstr, deviceObj, num, size);
            currentTime = System.currentTimeMillis();
        }
        // 返回离线数据
        dataMap.put("timestamp", currentTime);


        //接受内存参数memory
        String memory = request.getParameter("memory");
        String version = request.getParameter("version");
        //设备更新离线状态和内存、同步数据时间
        if (StringUtils.isNotEmpty(memory)) {
            deviceService.updeatOfflineParam(memory, deviceCode, version);
        }

        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.OFFLINE_SUCCESS);
        jsonResult.setData(dataMap);
        return jsonResult;
    }

    /**
     * 获取文件同步状态
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/api/offline/file", method = {RequestMethod.POST})
    @ResponseBody
    public Object shelfListFile(HttpServletRequest request, @RequestParam("token") String token) {


        Long currentTime = System.currentTimeMillis();
        //TOKEN
        if (StringUtil.isEmpty(token)) {
            return JsonResult.getError(APIConstants.TOKEN_NULL);
        }


        String deviceCode = request.getParameter("devcode");
        //检查设备号
        if (StringUtils.isEmpty(deviceCode)) {
            return JsonResult.getError(APIConstants.DEVICE_INVALID);
        }
        Device deviceObj = deviceService.getDeviceByCode(deviceCode);
        if (deviceObj == null) {
            return JsonResult.getError(APIConstants.NO_DEVICE);
        }
        if (deviceObj.getEnabled() != 1) {
            return JsonResult.getError(APIConstants.DEVICE_DISABLED);
        }
        //获取文件同步状态（start , end）
        String offLineStatus = request.getParameter("offLineStatus");
        if (!"start".equals(offLineStatus) && !"end".equals(offLineStatus)) {
            return JsonResult.getError(Constants.EXCEPTION);
        }
        deviceService.updateDeviceOffLine(offLineStatus, deviceCode);
        JsonResult jsonResult = JsonResult.getSuccess(APIConstants.STATUS_SUCCESS);
        return jsonResult;
    }


    /**
     * 大屏点击图书详情/添加阅读记录
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/offline/addIndexRecord", method = {RequestMethod.POST})
    @ResponseBody
    public Object addIndexRecord(HttpServletRequest request) {
        try {
            String book_id = request.getParameter("book_id");
            String member_id = request.getParameter("member_id");
            String org_id = request.getParameter("org_id");
            String record_type = request.getParameter("record_type");
            if (StringUtils.isEmpty(record_type)) {
                return JsonResult.getError("请提供统计类型");
            }
            if (StringUtils.isEmpty(member_id)) {
                return JsonResult.getError("请提供用户ID");
            }
            if (StringUtil.isEmpty(book_id)) {
                return JsonResult.getError("请提供图书ID");
            }
            if (StringUtils.isEmpty(org_id)) {
                return JsonResult.getError("请提供机构ID");
            }
            BookIndexRecord bookIndexRecord = new BookIndexRecord();
            bookIndexRecord.setBook_id(Long.parseLong(book_id));
            bookIndexRecord.setDevice_type_code("max");
            bookIndexRecord.setMember_id(Long.parseLong(member_id));
            bookIndexRecord.setOrg_id(Long.parseLong(org_id));
            bookIndexRecord.setRecord_type(Integer.parseInt(record_type));
            bookIndexRecord.setBook_type(2);
            bookIndexRecordService.addRecord(bookIndexRecord);
            JsonResult result = JsonResult.getSuccess(Constants.ACTION_ADD);
            result.setData(bookIndexRecord);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getException(Constants.EXCEPTION);
        }
    }

}
