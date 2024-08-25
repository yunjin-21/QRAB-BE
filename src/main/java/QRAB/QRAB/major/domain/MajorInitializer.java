package QRAB.QRAB.major.domain;

import QRAB.QRAB.major.repository.MajorRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration // 스프링의 구성 클래스를 나타냄
public class MajorInitializer {
    @Bean
    @Transactional
    public CommandLineRunner initMajor(MajorRepository majorRepository) {
        return args -> {
            // 자유계열
            String[] hokmaMajors = {
                    "인문사회자유전공",
                    "인문자연자유전공",
                    "인문사회융합전공",
                    "인문자연융합전공"
            };
            saveMajors(majorRepository, "자유계열", hokmaMajors);

            // 인문계열
            String[] humanitiesMajors = {
                    "국어국문학과",
                    "독일어문학과",
                    "러시아어문학과",
                    "스페인어문학과",
                    "언어학과",
                    "영미어문학과",
                    "일본어문학과",
                    "중국어문학과",
                    "프랑스어문학과",
                    "복수어문학과",
                    "기타동양어문학과",
                    "기타서양어문학과",
                    "문예창작학과",
                    "문화인류-민속-미술사학과",
                    "역사고고학과",
                    "종교학과",
                    "철학윤리학과",
                    "교양인문학부"
            };
            saveMajors(majorRepository, "인문계열", humanitiesMajors);

            // 사회계열
            String[] socialMajors = {
                    "경영학과",
                    "경영정보학과",
                    "경제학과",
                    "금융회계세무학과",
                    "무역유통학과",
                    "관광학과",
                    "광고홍보소비자학과",
                    "법학과",
                    "아동가족복지학과",
                    "국제학과",
                    "도시지리부동산학과",
                    "문헌정보학과",
                    "사회학과",
                    "심리학과",
                    "언론방송매체학과",
                    "정치외교학과",
                    "행정학과",
                    "경찰소방군사학과"
            };
            saveMajors(majorRepository, "사회계열", socialMajors);

            // 교육계열
            String[] educationMajors = {
                    "유아교육학과",
                    "초등교육학과",
                    "교육학과",
                    "영어교육학과",
                    "언어교육학과",
                    "특수교육학과",
                    "사회교육학과",
                    "국어교육학과"
            };
            saveMajors(majorRepository, "교육계열", educationMajors);


            // 자유계열
            String[] hokmaMajors2 = {
                    "자연과학자유전공",
                    "인문자연자유전공",
                    "자연과학융합전공",
                    "인문자연웅합전공"
            };
            saveMajors(majorRepository, "자유계열", hokmaMajors2);

            // 교육계열
            String[] educationMajors2 = {
                    "수학교육학과",
                    "공학교육학과",
                    "과학교육학과",
                    "자연교육학과"
            };
            saveMajors(majorRepository, "교육계열", educationMajors2);

            // 공학계열
            String[] engineeringMajors = {
                    "건축·설비공학과",
                    "건축학과",
                    "도시공학과",
                    "조경학과",
                    "토목공학과",
                    "환경공학과",
                    "기계·기전공학과",
                    "자동차공학과",
                    "조선·해양공학과",
                    "철도·지상교통공학과",
                    "항공·우주공학과",
                    "항공운항·조종학과",
                    "산업공학과",
                    "안전·방재공학과",
                    "신소재·재료공학과",
                    "반도체공학과",
                    "컴퓨터·소프트웨어공학과",
                    "의료공학과",
                    "전기·전자공학과",
                    "정보·통신공학과",
                    "고분자·화학공학과",
                    "생명공학과",
                    "에너지공학과",
                    "교양공학부"
            };
            saveMajors(majorRepository, "공학계열", engineeringMajors);

            // 자연계열
            String[] naturalMajors = {
                    "농업·작물·산림·원예학과",
                    "식품가공학과",
                    "동물·축산학과",
                    "미용과학과",
                    "식품영양·조리학과",
                    "의류·의상학과",
                    "주거학과",
                    "생물·생명과학과",
                    "화학과",
                    "물리학과",
                    "수학과",
                    "지구과학·해양학과",
                    "천문·기상학과",
                    "통계학과",
                    "환경학과",
                    "교양자연학부"
            };
            saveMajors(majorRepository, "자연계열", naturalMajors);
            // 의약계열
            String[] medicalMajors = {
                    "간호학과",
                    "약학과",
                    "의예과",
                    "치의예과",
                    "한의예과",
                    "재활학과",
                    "임상보건학과",
                    "약과학과",
                    "수의예과",
                    "보건학과"
            };
            saveMajors(majorRepository, "의약계열", medicalMajors);

            // 교육계열
            String[] educationMajors3 = {
                    "체육교육학과",
                    "음악교육학과",
                    "미술교육학과"
            };
            saveMajors(majorRepository, "교육계열", educationMajors3);

            // 예체능계열
            String[] artsAndPEMajors = {
                    "기타디자인학과",
                    "디자인일반학과",
                    "산업디자인학과",
                    "시각디자인학과",
                    "패션디자인학과",
                    "무용학과",
                    "순수체육학과",
                    "체육응용학과",
                    "순수미술학과",
                    "응용미술학과",
                    "연극·영화·방송학과",
                    "국악학과",
                    "기악과",
                    "성악과",
                    "실용음악학과",
                    "음악학과",
                    "작곡과",
                    "영상콘텐츠학과"
            };
            saveMajors(majorRepository, "예체능계열", artsAndPEMajors);

        };
    }
        private void saveMajors(MajorRepository majorRepository, String department, String[] majors){
            for (String majorName : majors) {
                if (!majorRepository.existsByName(majorName)) {
                    majorRepository.save(new Major(department, majorName));
                }
            }
        }
}