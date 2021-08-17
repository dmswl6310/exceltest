import "./App.css";
import { fileMapping } from "./fileMapping";
import { parcingExcel } from "./parcingExcel";
const fileChangedHandler = async (event: { target: { files: any } }) => {
  // groupName과 path 알기
  console.log("선택한 file : ", event.target.files);
  const splitName = event.target.files[0].name.split(" -  ");
  if (splitName.length !== 2) console.log("파일 이름 에러");

  const groupName = splitName[0];
  const groupPath = event.target.files[0].path;
  console.log(
    "prefix명 :",
    groupName,
    "                 파일위치 :",
    groupPath
  );
  let excelData = await fileMapping(event.target.files);
  console.log("excelData", excelData);
  console.log("eef", excelData[0][0]);
  // return parcingExcel(excelData);
};

function App() {
  return (
    <div>
      <p>엑셀 파일열기~~~~~~!!</p>211
      <input
        type="file"
        onChange={fileChangedHandler}
        multiple
        accept=".xlsx,.xls"
      />
    </div>
  );
}

export default App;
