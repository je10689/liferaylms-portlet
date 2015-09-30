create table Lms_AuditEntry (
	auditId LONG not null primary key,
	auditDate DATE null,
	companyId LONG,
	groupId LONG,
	userId LONG,
	classname VARCHAR(75) null,
	action VARCHAR(75) null,
	extradata TEXT null,
	classPK LONG,
	associationClassPK LONG
);

create table Lms_CheckP2pMailing (
	checkP2pId LONG not null primary key,
	actId LONG,
	date_ DATE null
);

create table Lms_Competence (
	uuid_ VARCHAR(75) null,
	competenceId LONG not null primary key,
	companyId LONG,
	groupId LONG,
	userId LONG,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	title STRING null,
	description STRING null,
	page VARCHAR(75) null,
	generateCertificate BOOLEAN,
	diplomaTemplate STRING null
);

create table Lms_Course (
	uuid_ VARCHAR(75) null,
	courseId LONG not null primary key,
	companyId LONG,
	groupId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	groupCreatedId LONG,
	createDate DATE null,
	modifiedDate DATE null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	title STRING null,
	description STRING null,
	friendlyURL VARCHAR(100) null,
	startDate DATE null,
	endDate DATE null,
	icon LONG,
	CourseEvalId LONG,
	CourseExtraData TEXT null,
	closed BOOLEAN,
	maxusers LONG,
	calificationType LONG,
	welcome BOOLEAN,
	welcomeMsg TEXT null,
	welcomeSubject VARCHAR(75) null,
	goodbye BOOLEAN,
	goodbyeMsg TEXT null,
	goodbyeSubject VARCHAR(75) null
);

create table Lms_CourseCompetence (
	uuid_ VARCHAR(75) null,
	CourcompetenceId LONG not null primary key,
	courseId LONG,
	competenceId LONG,
	condition_ BOOLEAN
);

create table Lms_CourseResult (
	crId LONG not null primary key,
	courseId LONG,
	result LONG,
	comments VARCHAR(75) null,
	userId LONG,
	passed BOOLEAN,
	startDate DATE null,
	passedDate DATE null,
	allowStartDate DATE null,
	allowFinishDate DATE null
);

create table Lms_LearningActivity (
	uuid_ VARCHAR(75) null,
	actId LONG not null primary key,
	companyId LONG,
	userId LONG,
	groupId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	title STRING null,
	description STRING null,
	typeId INTEGER,
	startdate DATE null,
	enddate DATE null,
	precedence LONG,
	tries LONG,
	passpuntuation INTEGER,
	priority LONG,
	moduleId LONG,
	extracontent TEXT null,
	feedbackCorrect VARCHAR(75) null,
	feedbackNoCorrect VARCHAR(75) null,
	weightinmodule LONG,
	commentsActivated BOOLEAN
);

create table Lms_LearningActivityResult (
	uuid_ VARCHAR(75) null,
	larId LONG not null primary key,
	actId LONG,
	userId LONG,
	result LONG,
	startDate DATE null,
	endDate DATE null,
	latId LONG,
	comments TEXT null,
	passed BOOLEAN
);

create table Lms_LearningActivityTry (
	uuid_ VARCHAR(75) null,
	latId LONG not null primary key,
	actId LONG,
	userId LONG,
	startDate DATE null,
	result LONG,
	endDate DATE null,
	endUserDate DATE null,
	tryData VARCHAR(75) null,
	tryResultData TEXT null,
	comments TEXT null
);

create table Lms_LmsPrefs (
	companyId LONG not null primary key,
	teacherRole LONG,
	editorRole LONG,
	lmsTemplates VARCHAR(75) null,
	activities VARCHAR(75) null,
	courseevals VARCHAR(75) null,
	scoretranslators VARCHAR(75) null,
	usersResults LONG
);

create table Lms_Module (
	uuid_ VARCHAR(75) null,
	moduleId LONG not null primary key,
	companyId LONG,
	groupId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	title STRING null,
	description STRING null,
	ordern LONG,
	startDate DATE null,
	endDate DATE null,
	icon LONG,
	precedence LONG,
	allowedTime LONG
);

create table Lms_ModuleResult (
	moduleId LONG,
	result LONG,
	comments VARCHAR(75) null,
	userId LONG,
	startDate DATE null,
	passed BOOLEAN,
	mrId LONG not null primary key,
	passedDate DATE null
);

create table Lms_P2pActivity (
	uuid_ VARCHAR(75) null,
	p2pActivityId LONG not null primary key,
	actId LONG,
	userId LONG,
	fileEntryId LONG,
	countCorrections LONG,
	description TEXT null,
	date_ DATE null,
	asignationsCompleted BOOLEAN
);

create table Lms_P2pActivityCorrections (
	uuid_ VARCHAR(75) null,
	p2pActivityCorrectionsId LONG not null primary key,
	p2pActivityId LONG,
	userId LONG,
	actId LONG,
	description TEXT null,
	date_ DATE null,
	fileEntryId LONG,
	result LONG
);

create table Lms_SCORMContent (
	uuid_ VARCHAR(75) null,
	scormId LONG not null primary key,
	companyId LONG,
	groupId LONG,
	userId LONG,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	title VARCHAR(75) null,
	description TEXT null,
	index_ VARCHAR(75) null,
	ciphered BOOLEAN
);

create table Lms_SurveyResult (
	uuid_ VARCHAR(75) null,
	surveyResultId LONG not null primary key,
	actId LONG,
	latId LONG,
	questionId LONG,
	answerId LONG,
	userId LONG,
	freeAnswer VARCHAR(75) null
);

create table Lms_TestAnswer (
	uuid_ VARCHAR(75) null,
	answerId LONG not null primary key,
	questionId LONG,
	actId LONG,
	precedence LONG,
	answer TEXT null,
	isCorrect BOOLEAN,
	feedbackCorrect VARCHAR(1000) null,
	feedbacknocorrect VARCHAR(1000) null
);

create table Lms_TestQuestion (
	uuid_ VARCHAR(75) null,
	questionId LONG not null primary key,
	actId LONG,
	text_ TEXT null,
	questionType LONG,
	weight LONG,
	extracontent TEXT null
);

create table Lms_UserCompetence (
	uuid_ VARCHAR(75) null,
	usercompId LONG not null primary key,
	userId LONG,
	competenceId LONG,
	compDate DATE null,
	courseId LONG
);