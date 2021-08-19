const xlsx = require("xlsx");

const fileMapping = async (files: any[]) => {
  const pattern = [
    /Quantitation Plate View Results.xlsx$/,
    /Melt Curve Plate View Results.xlsx$/,
    /Melt Curve Peak Results.xlsx$/,
    /Quantitation Ct Results.xlsx$/,
    /Quantitation Cq Results.xlsx$/,
    /Quantitation Summary.xlsx$/,
    /Quantitation Amplification Results.xlsx$/,
    /Melt Curve Derivative Results.xlsx$/,
    /Melt Curve Amplification Results.xlsx$/,
  ];
  const fileTurn = new Array(pattern.length);
  fileTurn.fill(0);

  let channelName;
  // 파일을 보면서, 몇번째 매칭되는 파일인지 인덱스를 저장한다. (없는 파일은 0)
  for (let j = 0; j < files.length; j += 1) {
    for (let i = 0; i < pattern.length; i += 1) {
      if (pattern[i].test(files[j].name)) {
        const data = await readSynchronous(files[j]);
        const workbook = xlsx.read(data, { type: "binary" });

        const sheetNum = workbook.SheetNames.length;
        if (i === 0) {
          // ** 채널 이름을 plate view result의 시트별 이름에서 읽어온다.
          channelName = Object.values(workbook.SheetNames);
        }

        const sheetArr = new Array(sheetNum);

        for (let j = 0; j < sheetNum; j += 1) {
          sheetArr[j] = xlsx.utils.sheet_to_row_object_array(
            workbook.Sheets[workbook.SheetNames[j]]
          );
        }
        fileTurn[i] = JSON.parse(JSON.stringify(sheetArr));

        break;
      }
      if (i === pattern.length - 1) console.log("not mapping name");
    }
  }
  console.log("읽은파일", fileTurn);

  return [fileTurn, channelName];
};

const readSynchronous = async (file: any) => {
  let resultBinary = await new Promise((resolve) => {
    let fileReader = new FileReader();
    fileReader.onload = (e) => resolve(fileReader.result);
    fileReader.readAsBinaryString(file);
  });
  return resultBinary;
};

export { fileMapping };

// Object 출력
// console.log(rowObject);

// String 출력
// console.log(JSON.stringify(rowObject));

// 파일순으로 매핑할려면...!
//     const parcingExcel = async (wholeExcel: any, index: number) => {
//   // console.log(wholeExcel)
//   switch (index) {
//     case 0:
//       plate=initWellInfo(wholeExcel)
//       break;
//     case 1:
//       initMeltPeakInfo(wholeExcel)
//       break;
//     case 2:
//       initWellName(wholeExcel)
//       break;
//     case 3: case 4:
//       initCtInfo(wholeExcel);
//       break;
//     case 5:
//       initRunInfo(wholeExcel);
//       break;
//     case 6:
//       initCycleInfo(plate,wholeExcel);
//       break;
//     case 7:
//       initTemperatureInfo(wholeExcel);
//       break;
//     case 8:
//       initMeltCurveInfo(wholeExcel);
//       break;
//     default:
//       console.log("file error")
//   }
// };
