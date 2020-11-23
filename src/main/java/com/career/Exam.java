package com.career;

public class Exam {
	
	private String student_name;
	private String exam_date;
	private String student_code;
	private Integer exam_grade;
	
		
	public Exam(String student_name, String student_code, String exam_date, Integer exam_grade) {
		super();
		this.student_name = student_name;
		this.exam_date = exam_date;
		this.student_code = student_code;
		this.exam_grade = exam_grade;
	}
	
	public String getStudent_name() {
		return student_name;
	}
	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}
	public String getExam_date() {
		return exam_date;
	}
	public void setExam_date(String exam_date) {
		this.exam_date = exam_date;
	}
	public String getStudent_code() {
		return student_code;
	}

	public void setStudent_code(String student_code) {
		this.student_code = student_code;
	}
	public Integer getExam_grade() {
		return exam_grade;
	}
	public void setExam_grade(Integer exam_grade) {
		this.exam_grade = exam_grade;
	}
	
	@Override
	public String toString() {
		return "Exam [student_name=" + student_name + ", exam_date=" + exam_date + ", student_code=" + student_code
				+ ", exam_grade=" + exam_grade + "]";
	}

}
