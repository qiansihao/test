package org.surpass.garlic.excle;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.surpass.common.constant.GlobalConstant;
import org.surpass.tsp.utils.UtilFile;


public class EmpEvents {
	public static final String module = EmpEvents.class.getName();

	public static String create(HttpServletRequest request,
			HttpServletResponse response) {
		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");
  
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		try {
			String type = "emp";
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = upload.parseRequest(request);
			Map<String, Object> context = UtilFile.uploadExerImage(items);
			context.put("empfile", context.get("filePath"));
			context.put("userLogin", userLogin);
			String diapatch_url = "";
				diapatch_url = "manage_employee_detail";	
			ModelService pService = dispatcher.getDispatchContext()
					.getModelService(diapatch_url);
			context = pService.makeValid(context, ModelService.IN_PARAM);
			Map result = dispatcher.runSync(pService.name, context);
			request.setAttribute(GlobalConstant.CUSTOM_JSON,
					result.get(GlobalConstant.CUSTOM_JSON));
		} catch (Exception e) {
			Debug.logError(e, module);
			return ModelService.RESPOND_ERROR;
		}
		return ModelService.RESPOND_SUCCESS;
	}
}
