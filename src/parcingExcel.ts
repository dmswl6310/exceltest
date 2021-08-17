type resultDataFormat = {
  version: string;
  name: string;
  path: string;
  date: string;
  PCRInstrumentSerial: string;
  launcherVersion: string;
  plate: plateFormat;
  data: dataFormat[];
};

type plateFormat = {
  tubePlate: string;
  capFilm: string;
  wellCount: number;
  barcodePlate: string;
  barcodeDWP: string;
  wells: wellFormat[];
};

type wellFormat = {
  position: string;
  type: string;
  productName: string;
  productNameAbbr: string;
  extractionType: number;
  name: string;
  patientID: string;
  urine: boolean;
  clotSample: boolean;
  barcodeExtract: string;
  barcodePCR: {
    indexSet: number;
  };
};

type dataFormat = {
  channel: number;
  dye: string;
  stepData: stepDataFormat[];
};

type stepDataFormat = {
  step: number;
  stepType: string;
  stepInfo: stepInfoFormat;
  wellData: wellDataFormat[];
};

type stepInfoFormat = {
  start: number;
  end: number;
  increment: number;
};

type wellDataFormat = {
  position: string;
  target: string;
  Sq: number;
  Ct: number;
  quantification: Array<number>;
};

const parcingExcel = async (data: any[]) => {
  // console.log("data는...",data)
  // console.log("data",data.length)
  // console.log("d[1]]",data[0][0][0])
  // console.log("d[0][0]",data[0][0])
  let channelNum = 5;
  channelNum = data[0].length;
  const result = {} as resultDataFormat;
  result.name = "파일 검색시 조사";
  result.path = "파일 검색시 조사";
  result.date = "파일 검색시 조사";

  const plate = {} as plateFormat;
  let wellcount = 0;
  // wellCount를 세면서 wellFormat[]배열의 원소를 필요한만큼 생성하면서 type채움
  for (let i = 0; i < data.length; i += 4) {
    console.log("defefefe", data[0]);
    console.log("defefefe", data[0][0]);
    const rowValue = data[0][0][i]["__EMPTY"];
    const keys = Object.keys(data[0][0][i]);
    const rowwell = data[0][0][i].length - 2;
    wellcount += rowwell;
    for (let j = 0; j < rowwell; j += 1) {
      const well = {} as wellFormat;
      well.position = rowValue + keys[j];
      if (data[0][0][i + 1][keys[j]] === "Unkn") {
        well.position = "SAMPLE";
      } else if (data[0][0][i + 1][keys[j]] === "Pos Ctrl") {
        well.position = "PC";
      } else if (data[0][0][i + 1][keys[j]] === "Neg Ctrl") {
        well.position = "NC";
      } else {
        console.log("error");
      }
      plate.wells.push(well);
      // console.log(well)
    }
  }
  plate.wellCount = wellcount;
  result.plate = plate;

  for (let i = 0; i < channelNum; i += 1) {
    const Data = {} as dataFormat;

    Data.channel = i; // DB 매핑필요..
    Data.dye = "e"; //시트이름 미리 받아놔야..

    let stepNum = 5; //불필요
    for (let j = 0; j < stepNum; j += 1) {
      const stepData = {} as stepDataFormat;
      stepData.step = j; //디렉토리 읽을때 미리 step가져와야
      stepData.stepType = "wow"; //디렉토리 읽을때 미리 step가져와야

      const stepinfo = {} as stepInfoFormat;
      if (stepData.stepType === "QuanStep") {
        stepinfo.start = data[6][i][0]["Cycle"];
        stepinfo.end = data[6][i][data[6][i].length - 1]["Cycle"];
        stepinfo.increment = data[6][i][1]["Cycle"] - data[6][i][0]["Cycle"];
      } else if (stepData.stepType === "MeltStep") {
        stepinfo.start = data[8][i][0]["Temperature"];
        stepinfo.end = data[8][i][data[8][i].length - 1]["Temperature"];
        stepinfo.increment =
          data[8][i][1]["Cycle"] - data[8][i][0]["Temperature"];
      } else {
        console.log("미확인 stepType");
      }
      stepData.stepInfo = stepinfo;

      for (let k = wellcount * i; k < wellcount * (i + 1); k++) {
        const welldata = {} as wellDataFormat;
        welldata.position = data[3][0][k]["Well"];

        // Starting Quantity (SQ) 키가 없을때 null로 예외처리해야함.
        welldata.Sq = data[3][0][k]["Starting Quantity (SQ)"];
        // Starting Quantity (SQ) 키가 없을때 null로 예외처리해야함.
        welldata.Ct = data[3][0][k]["Well"];

        if (stepData.stepType === "QuanStep") {
          for (let m = 0; m < stepinfo.end; m += 1) {
            welldata.quantification.push(data[6][i][m][welldata.position]);
          }
        } else if (stepData.stepType === "MeltStep") {
          for (let m = 0; m < stepinfo.end; m += 1) {
            welldata.quantification.push(data[8][i][m][welldata.position]);
          }
        } else {
          console.log("미확인 stepType");
        }

        stepData.wellData.push(welldata);
      }

      Data.stepData.push(stepData);
    }
    result.data.push(Data);
  }
};

export { parcingExcel };
