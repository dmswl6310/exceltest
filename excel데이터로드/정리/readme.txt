PCR 장비 (CFX96, SGRT 등) 에서의 결과는 장비마다 다른 포맷으로 구성되어 있고, 정보의 종류도 다르게 담고 있다.
CFX96 장비의 경우 PLRN 연동 기능도 고려하여야 한다.

본 폴더는 장비별 파싱한 데이터를 공통된 데이터 포맷으로 뷰어에게 전달하여야 하므로,
이러한 공통된 데이터 구조를 설계한 내용을 담고 있다.

최종 산출물은 다음 파일들을 참조하면 된다.
format.json : 공통 데이터 구조 정의 최종 결과물
	Ex) "Path":		"String	위 Name 을 선택한 Path"
		Field Name : Path
		Type : String
		필드 내용 설명부 : 위 Name 을 선택한 Path

format_example.json : format.json 에 대한 Example

CFX96_Data_Read_AND_PRLN_Data_Read.xlsx, SGRT_Data_Format.xlsx : 회의 때 format.json 도출을 위해 함께 논의한 파일로써 보지 않아도 됨.

PLRN_Type 관련.txt : Well Type 에 대한 부가 설명
