package com.github.rahulsom.cda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * List of CCD Section Codes
 *
 * @author Rahul Somasunderam
 */
public class CcdSections {

    public static final String DCM = "2.16.840.1.113883.10.20.6.1.1";
    public static final String LOINC = "2.16.840.1.113883.6.1";

    private static final Map<String, String> codeSystemNames = new HashMap<String, String>();

    static {
        codeSystemNames.put(DCM, "DCM");
        codeSystemNames.put(LOINC, "LOINC");
    }

    private static final List<CE> sections = new ArrayList<CE>();

    private static CE code(String code, String displayName, String oid) {
        String codeSystemName = codeSystemNames.get(oid);
        CE ce = new CE().
                withCode(code).withDisplayName(displayName).
                withCodeSystem(oid).withCodeSystemName(codeSystemName);
        sections.add(ce);
        return ce;
    }

    public static final CE AdvanceDirectives = code("42348-3", "Advance Directives", LOINC);
    public static final CE Addendum = code("55107-7", "Addendum", LOINC);
    public static final CE Allergies = code("48765-2", "Allergies", LOINC);
    public static final CE Anesthesia = code("59774-0", "Anesthesia", LOINC);
    public static final CE Assessment = code("51848-0", "Assessment", LOINC);
    public static final CE AssessmentAndPlan = code("51847-2", "Assessment and Plan", LOINC);
    public static final CE ChiefComplaint = code("10154-3", "Chief Complaint", LOINC);
    public static final CE ChiefComplaintAndReasonForVisit = code("46239-0", "Chief Complaint and Reason for Visit", LOINC);
    public static final CE ClinicalPresentation = code("55108-5", "Clinical Presentation", LOINC);
    public static final CE Complications = code("55109-3", "Complications", LOINC);
    public static final CE Conclusions = code("55110-1", "Conclusions", LOINC);
    public static final CE CurrentImagingProcedureDescriptions = code("55111-9", "Current Imaging Procedure Descriptions", LOINC);
    public static final CE DicomObjectCatalog = code("121181", "DICOM Object Catalog", DCM);
    public static final CE DischargeDiet = code("42344-2", "Discharge Diet", LOINC);
    public static final CE DocumentSummary = code("55112-7", "Document Summary", LOINC);
    public static final CE Encounters = code("46240-8", "Encounters", LOINC);
    public static final CE FamilyHistory = code("10157-6", "Family History", LOINC);
    public static final CE FindingsRadiologyStudyObservation = code("18782-3", "Findings (Radiology Study - Observation)", LOINC);
    public static final CE FunctionalStatus = code("47420-5", "Functional Status", LOINC);
    public static final CE GeneralStatus = code("10210-3", "General Status", LOINC);
    public static final CE HistoryOfPastIllnessPastMedicalHistory = code("11348-0", "History of Past Illness (Past Medical History)", LOINC);
    public static final CE HistoryOfPresentIllness = code("10164-2", "History of Present Illness", LOINC);
    public static final CE HospitalAdmissionDiagnosis = code("46241-6", "Hospital Admission Diagnosis", LOINC);
    public static final CE HospitalConsultation = code("18841-7", "Hospital Consultation", LOINC);
    public static final CE HospitalCourse = code("8648-8", "Hospital Course", LOINC);
    public static final CE HospitalDischargeDiagnosis = code("11535-2", "Hospital Discharge Diagnosis", LOINC);
    public static final CE HospitalDischargeInstructions = code("8653-8", "Hospital Discharge Instructions", LOINC);
    public static final CE HospitalDischargeMedications = code("10183-2", "Hospital Discharge Medications", LOINC);
    public static final CE HospitalDischargePhysical = code("10184-0", "Hospital Discharge Physical", LOINC);
    public static final CE HospitalDischargeStudiesSummary = code("11493-4", "Hospital Discharge Studies Summary", LOINC);
    public static final CE Immunizations = code("11369-6", "Immunizations", LOINC);
    public static final CE Interventions = code("62387-6", "Interventions", LOINC);
    public static final CE KeyImages = code("55113-5", "Key Images", LOINC);
    public static final CE MedicalEquipment = code("46264-8", "Medical Equipment", LOINC);
    public static final CE MedicalGeneralHistory = code("11329-0", "Medical (General) History", LOINC);
    public static final CE Medications = code("10160-0", "Medications", LOINC);
    public static final CE MedicationsAdministered = code("29549-3", "Medications Administered ", LOINC);
    public static final CE Objective = code("61149-1", "Objective", LOINC);
    public static final CE OperativeNoteFluids = code("10216-0", "Operative Note Fluids", LOINC);
    public static final CE OperativeNoteSurgicalProcedure = code("10223-6", "Operative Note Surgical Procedure", LOINC);
    public static final CE Payers = code("48768-6", "Payers", LOINC);
    public static final CE PhysicalExam = code("29545-1", "Physical Exam", LOINC);
    public static final CE PlanOfCare = code("18776-5", "Plan of Care", LOINC);
    public static final CE PlannedProcedure = code("59772-4", "Planned Procedure", LOINC);
    public static final CE PostOperativeDiagnosis = code("10218-6", "Post-operative Diagnosis", LOINC);
    public static final CE PostProcedureDiagnosis = code("59769-0", "Post-procedure Diagnosis", LOINC);
    public static final CE PreoperativeDiagnosis = code("10219-4", "Preoperative Diagnosis", LOINC);
    public static final CE PriorImagingProcedureDescriptions = code("55114-3", "Prior Imaging Procedure Descriptions", LOINC);
    public static final CE Problem = code("11450-4", "Problem ", LOINC);
    public static final CE ProcedureDescription = code("29554-3", "Procedure Description", LOINC);
    public static final CE ProcedureDisposition = code("59775-7", "Procedure Disposition", LOINC);
    public static final CE ProcedureEstimatedBloodLoss = code("59770-8", "Procedure Estimated Blood Loss", LOINC);
    public static final CE ProcedureFindings = code("59776-5", "Procedure Findings", LOINC);
    public static final CE ProcedureImplants = code("59771-6", "Procedure Implants", LOINC);
    public static final CE ProcedureIndications = code("59768-2", "Procedure Indications ", LOINC);
    public static final CE ProcedureSpecimensTaken = code("59773-2", "Procedure Specimens Taken", LOINC);
    public static final CE ProceduresListOfSurgeriesHistoryOfProcedures = code("47519-4", "Procedures List of Surgeries (History of Procedures) ", LOINC);
    public static final CE RadiologyComparisonStudyObservation = code("18834-2", "Radiology Comparison Study – Observation", LOINC);
    public static final CE RadiologyImpression = code("19005-8", "Radiology – Impression", LOINC);
    public static final CE RadiologyStudyRecommendations = code("18783-1", "Radiology Study – Recommendations", LOINC);
    public static final CE RadiologyReasonForStudy = code("18785-6", "Radiology Reason for Study", LOINC);
    public static final CE ReasonForReferral = code("42349-1", "Reason for Referral", LOINC);
    public static final CE ReasonForVisit = code("29299-5", "Reason for Visit", LOINC);
    public static final CE RequestedImagingStudiesInformation = code("55115-0", "Requested Imaging Studies Information", LOINC);
    public static final CE Results = code("30954-2", "Results", LOINC);
    public static final CE ReviewOfSystems = code("10187-3", "Review of Systems", LOINC);
    public static final CE SocialHistory = code("29762-2", "Social History", LOINC);
    public static final CE Subjective = code("61150-9", "Subjective ", LOINC);
    public static final CE SurgicalDrains = code("11537-8", "Surgical Drains", LOINC);
    public static final CE VitalSigns = code("8716-3", "Vital Signs ", LOINC);

    public static CE find(String codeSystem, String code) {
        for (CE section: sections) {
            if (section.getCode().equals(code) && section.getCodeSystem().equals(codeSystem)) {
                return section;
            }
        }
        return null;
    }
}
