package com.liferay.lms.learningactivity.questiontype;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.portlet.ActionRequest;

import com.liferay.lms.model.TestAnswer;
import com.liferay.lms.model.TestQuestion;
import com.liferay.lms.service.LearningActivityLocalServiceUtil;
import com.liferay.lms.service.LearningActivityTryLocalServiceUtil;
import com.liferay.lms.service.TestAnswerLocalService;
import com.liferay.lms.service.TestAnswerLocalServiceUtil;
import com.liferay.lms.service.TestQuestionLocalServiceUtil;
import com.liferay.util.portlet.PortletProps;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.theme.ThemeDisplay;

public class OptionsQuestionType extends BaseQuestionType {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactoryUtil.getLog(OptionsQuestionType.class);
	protected String inputType = "radio";
	protected String XMLSingle = "true";

	public long getTypeId(){
		return 0;
	}

	public String getName() {
		return "options";
	}

	@Override
	public boolean isInline() {
		return true;
	}

	public String getTitle(Locale locale) {
		return LanguageUtil.get(locale, "options.title");
	}

	public String getDescription(Locale locale) {
		return LanguageUtil.get(locale, "options.description");
	}
	
	public String getURLEdit(){
		return "/html/execactivity/test/admin/editAnswerOptions.jsp";
	}
	
	public String getURLNew(){
		return "/html/execactivity/test/admin/popups/options.jsp";
	}

	public boolean correct(ActionRequest actionRequest, long questionId){
		long[] answersId= ParamUtil.getLongValues(actionRequest, "question_"+questionId);
		List<Long> arrayAnswersId = new ArrayList<Long>();
		for(long answerId:answersId) arrayAnswersId.add(answerId);
		List<TestAnswer> testAnswers = new ArrayList<TestAnswer>();
		try {
			testAnswers = TestAnswerLocalServiceUtil.getTestAnswersByQuestionId(questionId);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		int correctAnswers=0, correctAnswered=0, incorrectAnswered=0;
		for(TestAnswer answer:testAnswers){
			if(isCorrect(answer)){
				correctAnswers++;
				if(arrayAnswersId.contains(answer.getAnswerId())) correctAnswered++;
			}else if(arrayAnswersId.contains(answer.getAnswerId())) incorrectAnswered++;
		}

		if(isQuestionCorrect(correctAnswers, correctAnswered, incorrectAnswered))return true;
		else return false;
	}
	
	protected boolean isQuestionCorrect(int correctAnswers, int correctAnswered, int incorrectAnswered){
		return correctAnswered>0 && incorrectAnswered==0;
	}

	protected boolean isCorrect(TestAnswer testAnswer){
		return (testAnswer!=null)?testAnswer.isIsCorrect():false;
	}

	public String getHtmlView(long questionId, ThemeDisplay themeDisplay, Document document){
		return getHtml(document, questionId, false, themeDisplay);
	}

	public Element getResults(ActionRequest actionRequest, long questionId){
		long[] answersId= ParamUtil.getLongValues(actionRequest, "question_"+questionId);

		List<Long> arrayAnswersId = new ArrayList<Long>();
		for(long answerId:answersId) arrayAnswersId.add(answerId);

		Element questionXML=SAXReaderUtil.createElement("question");
		questionXML.addAttribute("id", Long.toString(questionId));

		long currentQuestionId = ParamUtil.getLong(actionRequest, "currentQuestionId");
		if (currentQuestionId == questionId) {
			questionXML.addAttribute("current", "true");
		}

		for(long answer:arrayAnswersId){
			if(answer >0){
				Element answerXML=SAXReaderUtil.createElement("answer");
				answerXML.addAttribute("id", Long.toString(answer));
				questionXML.add(answerXML);
			}
		}
		return questionXML;
	}

	private String getHtml(Document document, long questionId,boolean feedback, ThemeDisplay themeDisplay){
		String html = "", answersFeedBack="", feedMessage = "", cssclass="", selected="";
		String namespace = themeDisplay != null ? themeDisplay.getPortletDisplay().getNamespace() : "";
		boolean isCombo = false;
		String onclick = "";
		try {
			TestQuestion question = TestQuestionLocalServiceUtil.fetchTestQuestion(questionId);
			String formatType = "0";
			boolean enableorder = false;
			if(question.getExtracontent()!=null && !question.getExtracontent().trim().isEmpty()){
				try{
					Document xml = SAXReaderUtil.read(question.getExtracontent());
					Element ele = xml.getRootElement();
					formatType = (String) ele.element("formattype").getData();
					enableorder = StringPool.TRUE.equals(LearningActivityLocalServiceUtil.getExtraContentValue(question.getActId(),"enableorder"));
					if ( enableorder && formatType.equals(PortletProps.get("lms.question.formattype.horizontal")) ){
						cssclass="in-line ";
					}else if ( enableorder && formatType.equals(PortletProps.get("lms.question.formattype.combo")) ){
						isCombo=true;
					}
				}catch(DocumentException e){
					e.printStackTrace();
				}
			}
			List<TestAnswer> answersSelected=getAnswersSelected(document, questionId);
			List<TestAnswer> testAnswers= TestAnswerLocalServiceUtil.getTestAnswersByQuestionId(question.getQuestionId());
			int correctAnswers=0, correctAnswered=0, incorrectAnswered=0;
			if(feedback) feedMessage = LanguageUtil.get(themeDisplay.getLocale(),"answer-in-blank") ;
			int numAnswer=0;
			String disabled = "";
			if (isCombo){
				answersFeedBack+="<option value=\"\">"+LanguageUtil.get(themeDisplay.getLocale(),"learningactivity.embeddedtest.select")+"</option>";
			}
			for(TestAnswer answer:testAnswers){
				if(inputType.equals("checkbox")){
					onclick = "onclick=\""+namespace+"checkMaxNumberOfChecks('"+question.getQuestionId()+"','"+numAnswer+"')\"";
				}
				String correct="", checked="", showCorrectAnswer="false";
				disabled = "";
				if(feedback) {
					showCorrectAnswer = LearningActivityLocalServiceUtil.getExtraContentValue(question.getActId(), "showCorrectAnswer");
					String showCorrectAnswerOnlyOnFinalTryString = LearningActivityLocalServiceUtil.getExtraContentValue(question.getActId(), "showCorrectAnswerOnlyOnFinalTry");
					try {
						if ("true".equals(showCorrectAnswerOnlyOnFinalTryString) && LearningActivityTryLocalServiceUtil.canUserDoANewTry(question.getActId(), themeDisplay.getUserId())) {
							showCorrectAnswer = "false";
						}
					} catch (Exception e) {}
					disabled = "disabled='disabled'";
				}
				if(isCorrect(answer)){
					correctAnswers++;
					if("true".equals(showCorrectAnswer)) correct="font_14 color_cuarto negrita";
					if(answersSelected.contains(answer)){
						correctAnswered++;
						checked="checked='checked'";
						feedMessage=(!LanguageUtil.get(themeDisplay.getLocale(),"answer-in-blank").equals(feedMessage))?feedMessage+"<br/>"+answer.getFeedbackCorrect():answer.getFeedbackCorrect();
					}
				}else if(answersSelected.contains(answer)){
					incorrectAnswered++;
					checked="checked='checked'";
					selected="selected";
					feedMessage=(!LanguageUtil.get(themeDisplay.getLocale(),"answer-in-blank").equals(feedMessage))?feedMessage+"<br/>"+answer.getFeedbacknocorrect():answer.getFeedbacknocorrect();
				}

				if (isCombo){
					answersFeedBack += 	"<option " + selected + " value= \"" + answer.getAnswerId() + "\" >" +
											answer.getAnswer() +			
										"</option>";
				}else{
					answersFeedBack += "<div class=\"answer " + cssclass + correct + "\">" +
											"<label for=\""+namespace+"question_"+question.getQuestionId()+"_"+numAnswer+"\" />"+
											"<input "+onclick+" id=\""+namespace+"question_"+question.getQuestionId()+"_"+numAnswer+"\" type=\"" + inputType 
												+ "\" name=\""+namespace+"question_" + question.getQuestionId() + "\" " + checked + " value=\"" 
													+ answer.getAnswerId() +"\" " + disabled + ">" +
											"<div class=\"answer-options\">" + answer.getAnswer() + 
											"</div>" + 
										"</div>";
				}
				
				numAnswer++;
			}

			if(feedback){
				if(isQuestionCorrect(correctAnswers, correctAnswered, incorrectAnswered))	cssclass="correct ";
				else cssclass="incorrect ";
				
				answersFeedBack = "<div class=\"content_answer\">" + answersFeedBack + "</div>";
				if (!"".equals(feedMessage)) {
					answersFeedBack += "<div class=\"questionFeedback\">" + feedMessage + "</div>";
				}
			}
			
			if (isCombo){
				html += "<div class=\"question " + cssclass + "questiontype_" + getName() + "_select questiontype_" + getTypeId() + "\">" +
							"<input type=\"hidden\" name=\""+namespace+"question\" value=\"" + question.getQuestionId() + "\"/>"+
							"<div class=\"questiontext select\">" + question.getText() + "</div>" +
							"<div class=\"answer select\">" +
								"<select "+ disabled + "class=\"answer select\" id=\""+namespace+"question_"+question.getQuestionId()+"_"+numAnswer+"\" name=\""+namespace+"question_"+question.getQuestionId()+"\" />"+
									answersFeedBack +
								"</select>" +
							"</div>" +
						"</div>";
			}else{
				html += "<div class=\"question " + cssclass + "questiontype_" + getName() + " questiontype_" + getTypeId() + "\">" +
						"<input type=\"hidden\" name=\""+namespace+"question\" value=\"" + question.getQuestionId() + "\"/>"+
						"<div class=\"questiontext " + cssclass + "\">" + question.getText() + "</div>" +
							answersFeedBack +
						"</div>";	
			}
			
			
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return html;
	}

	public String getHtmlFeedback(Document document,long questionId, ThemeDisplay themeDisplay){
		return getHtml(document, questionId, true, themeDisplay);
	}

	protected List<TestAnswer> getAnswersSelected(Document document,long questionId){
		List<TestAnswer> answerSelected = new ArrayList<TestAnswer>();
		if(document != null){
			Iterator<Element> nodeItr = document.getRootElement().elementIterator();
			while(nodeItr.hasNext()) {
				Element element = nodeItr.next();
				if("question".equals(element.getName()) && questionId == Long.valueOf(element.attributeValue("id"))){
					Iterator<Element> elementItr = element.elementIterator();
					while(elementItr.hasNext()) {
						Element elementElement = elementItr.next();
						if("answer".equals(elementElement.getName())) {
							try {
								answerSelected.add(TestAnswerLocalServiceUtil.getTestAnswer(Long.valueOf(elementElement.attributeValue("id"))));
							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (PortalException e) {
								e.printStackTrace();
							} catch (SystemException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}	
		}
		return answerSelected;
	}
	
	public Element exportXML(long questionId) {
		XMLType="multichoice";
		Element questionXML = super.exportXML(questionId);
		try {
			List<TestAnswer> answers = TestAnswerLocalServiceUtil.getTestAnswersByQuestionId(questionId);
			for(TestAnswer answer:answers){
				Element answerE = SAXReaderUtil.createElement("answer");
				answerE.addAttribute("fraction", (answer.isIsCorrect())?"100":"0");
				
				Element text = SAXReaderUtil.createElement("text");
				text.addText(answer.getAnswer());
				answerE.add(text);
				
				Element feedback = SAXReaderUtil.createElement("feedback");
				Element feedText = SAXReaderUtil.createElement("text");
				feedText.addText(answer.getFeedbackCorrect());
				feedback.add(feedText);
				answerE.add(feedback);
				questionXML.add(answerE);
			}
			Element single = SAXReaderUtil.createElement("single");
			single.addText(XMLSingle);
			questionXML.add(single);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return questionXML;
	}

	public void importXML(long actId, Element question, TestAnswerLocalService testAnswerLocalService)throws SystemException, PortalException {
		Element questiontext=question.element("questiontext");
		String description=questiontext.elementText("text");
		TestQuestion theQuestion=TestQuestionLocalServiceUtil.addQuestion(actId,description,getTypeId());
		for(Element answerElement:question.elements("answer")){
			boolean correct=(!"0".equals(answerElement.attributeValue("fraction")))? true:false;
			String answer=answerElement.elementText("text");
			String feedback="";
			if(answerElement.element("feedback")!=null && answerElement.element("feedback").element("text")!=null)
				feedback=answerElement.element("feedback").element("text").getText();
			testAnswerLocalService.addTestAnswer(theQuestion.getQuestionId(), answer, feedback, feedback, correct);
		}
	}

	public int getMaxAnswers(){
		return GetterUtil.getInteger(PropsUtil.get("lms.maxAnswers.options"), 100);
	}
	public int getDefaultAnswersNo(){
		return GetterUtil.getInteger(PropsUtil.get("lms.defaultAnswersNo.options"), 2);
	}
	
}