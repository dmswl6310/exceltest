const xlsx = require("xlsx");

const fileMapping = async (files: any[]) => {
  console.log("files출력", files);
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
  // const fileTurn: any = [];
  fileTurn.fill(0);
  // console.log(fileTurn)

  // 파일을 보면서, 몇번째 매칭되는 파일인지 인덱스를 저장한다. (없는 파일은 0)
  for (let j = 0; j < files.length; j += 1) {
    for (let i = 0; i < pattern.length; i += 1) {
      if (pattern[i].test(files[j].name)) {
        // let index=fileTurn[i];
        const fileName = files[j].name;
        console.log(fileName, "읽기");

        const data = await readSynchronous(files[j]);
        const workbook = xlsx.read(data, { type: "binary" });
        // console.log(workbook);
        const sheetNum = workbook.SheetNames.length;
        const sheetArr = new Array(sheetNum);
        // const channelData = new Array<string>(sheetNum);
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
  console.log(fileTurn);

  return fileTurn;
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
// };
// }

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
