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
        const fileReader = new FileReader();
        const fileName = files[j].name;
        console.log(fileName, "읽기");

        fileReader.readAsBinaryString(files[j]);
        fileReader.onload = async (event) => {
          // typescript에서 null이 될 가능성을 있다고 판단하기에 처리해야함.
          const data = event.target ? event.target.result : null;
          if (data != null) {
            const workbook = xlsx.read(data, { type: "binary" });
            // console.log(workbook);
            const sheetNum = workbook.SheetNames.length;

            const sheetArr = new Array(sheetNum);

            // const channelData = new Array<string>(sheetNum);
            for (let j = 0; j < sheetNum; j += 1) {
              // channelData[j]=workbook.SheetNames[j];
              //  const rowObject = xlsx.utils.sheet_to_row_object_array(
              //    wholeExcel.Sheets[wholeExcel.SheetNames[j]],
              //  );
              sheetArr[j] = xlsx.utils.sheet_to_row_object_array(
                workbook.Sheets[workbook.SheetNames[j]]
              );
            }
            fileTurn[i] = JSON.parse(JSON.stringify(sheetArr));
            // console.log("sheetArr출력",0,"는",fileTurn[i][0][0])
          } else {
            console.log("data가 null");
          }
          // console.log("sheetArr출력", 1, "는", fileTurn[0][0][0]);
        };
        console.log("file안 ....sheetArr출력", fileTurn);
        // console.log("sheetArr출력", 2, "는", fileTurn[0][0][0]);
        break;
      }
      if (i === pattern.length - 1) console.log("not mapping name");
    }
  }
  // console.log("sheetArr출력", 0, "는", fileTurn[0][0][0]);
  // console.log("sheetArr출력", 1, "는", fileTurn[1][0][0]);
  // console.log("data는", fileTurn);
  // console.log("fileTurn은",fileTurn)
  // console.log(fileTurn[0]);
  return fileTurn;
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
