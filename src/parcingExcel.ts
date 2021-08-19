type resultDataFormat = {
  version?: string;
  name?: string;
  path?: string;
  date?: string;
  PCRInstrumentSerial?: string;
  launcherVersion?: string;
  launcherPosition?: string;
  plate?: plateFormat;
  data?: dataFormat[];
};

type plateFormat = {
  tubePlate?: string;
  capFilm?: string;
  wellCount: number;
  barcodePlate?: string;
  barcodeDWP?: string;
  wells?: wellFormat[];
};

type wellFormat = {
  position?: string;
  type: string;
  productName?: string;
  productNameAbbr?: string;
  extractionType?: number;
  name?: string;
  patientID?: string;
  urine?: boolean;
  clotSample?: boolean;
  barcodeExtract?: string;
  barcodePCR?: string[];
  indexSet?: number;
};

type dataFormat = {
  channel?: number;
  dye?: string;
  stepData?: stepDataFormat[];
};

type stepDataFormat = {
  step?: number;
  stepType?: string;
  stepInfo?: stepInfoFormat;
  wellData?: wellDataFormat[];
};

type stepInfoFormat = {
  start?: number;
  end?: number;
  increment?: number;
};

type wellDataFormat = {
  position: string;
  target?: string;
  Sq?: number;
  Ct?: number;
  quantification?: number[];
};

const parcingExcel = async (total: any[]) => {
  //  total[0] = 파일 읽은 거, total[1] = dye배열 (sheet별 이름)
  const data = total[0];

  const result = {} as resultDataFormat;
  result.version = "뷰어 버전";
  result.name = "파일 검색시 조사";
  result.path = "파일 검색시 조사";
  result.date = "파일 검색시 조사";
  result.PCRInstrumentSerial = "장비Serial";
  // result.launcherVersion = "PLRN";
  // result.launcherPosition = "PLRN";

  const plate = {} as plateFormat;
  // plate.tubePlate = "PLRN";
  // plate.capFilm = "PLRN";
  // plate.barcodeDWP = "PLRN";
  // plate.barcodePlate = "PLRN";

  plate.wells = [] as wellFormat[];
  // 파일이 존재하지 않을때 대안 필요!! (ex) plate view result가 없다면 ..?)

  // ** plate view result파일을 기준으로 wellCount 셈
  let wellcount = 0;

  // wellCount를 세면서 wellFormat[]배열의 원소를 필요한만큼 생성하면서 type채움
  for (let i = 0; i < data[0][0].length; i += 4) {
    const rowValue = data[0][0][i]["__EMPTY"];
    const keys = Object.keys(data[0][0][i]);

    // 데이터 있는 well만 원소로 들어가므로 갯수 한번에 더해도 돼
    const rowNum = keys.length - 2;
    wellcount += rowNum;

    for (let j = 0; j < rowNum; j += 1) {
      const well = {} as wellFormat;
      if (keys[j].length === 1) well.position = rowValue + "0" + keys[j];
      else well.position = rowValue + keys[j];

      if (data[0][0][i][keys[j]] === "Unkn") {
        well.type = "SAMPLE";
      } else if (data[0][0][i][keys[j]] === "Pos Ctrl") {
        well.type = "PC";
      } else if (data[0][0][i][keys[j]] === "Neg Ctrl") {
        well.type = "NC";
      } else {
        console.log("well.type값 error");
      }

      well.productName = "아직 해당기기 없음,";
      // well.productNameAbbr = "PLRN";
      // well.extractionType = 0;
      well.name = data[0][0][i + 1][keys[j]];

      // well.patientID = "PLRN";
      well.urine = false;
      well.clotSample = false;
      // well.barcodeExtract = "PLRN";
      // well.barcodePCR = [];
      // well.indexSet = 0;

      plate.wells.push(well);
    }
  }

  plate.wellCount = wellcount;
  result.plate = plate;

  result.data = [] as dataFormat[];

  const channelNum = total[1].length;
  const channelName = total[1];

  for (let i = 0; i < channelNum; i += 1) {
    const Data = {} as dataFormat;

    Data.channel = i; // DB? 매핑필요..
    Data.dye = channelName[i]; // 파일읽을때 미리 받은 시트별 이름(dye이름).

    let stepNum = 5; // 데이터 읽어올때 정의해야
    Data.stepData = [] as stepDataFormat[];
    for (let j = 0; j < stepNum; j += 1) {
      const stepData = {} as stepDataFormat;
      stepData.step = j; //디렉토리 읽을때 미리 step번호 가져와야
      stepData.stepType = "QuantStep"; //디렉토리 읽을때 미리 step가져와야

      const stepinfo = {} as stepInfoFormat;
      if (stepData.stepType === "QuantStep") {
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

      stepData.wellData = [] as wellDataFormat[];
      for (let k = wellcount * i; k < wellcount * (i + 1); k++) {
        const welldata = {} as wellDataFormat;

        // **quantitation Ct Result에서 가져옴
        welldata.position = data[3][0][k]["Well"];
        let pos = welldata.position;
        if (welldata.position[1] === "0")
          pos = welldata.position[0] + welldata.position[2];

        welldata.target = "SGRT, 미구현";
        welldata.Sq = data[3][0][k]["Starting Quantity (SQ)"];
        welldata.Ct = data[3][0][k]["C(t) Mean"];

        welldata.quantification = [] as number[];
        // **quantitation amplification results에서 가져옴
        if (stepinfo.end === undefined) {
          console.log("stepInfo undefined...");
        } else {
          if (stepData.stepType === "QuantStep") {
            for (let m = 0; m < stepinfo.end; m += 1) {
              welldata.quantification.push(data[6][i][m][pos]);
            }
          } else if (stepData.stepType === "MeltStep") {
            for (let m = 0; m < stepinfo.end; m += 1) {
              welldata.quantification.push(data[8][i][m][pos]);
            }
          } else {
            console.log("미확인 stepType");
          }
          stepData.wellData.push(welldata);
        }
      }
      Data.stepData.push(stepData);
    }
    result.data.push(Data);
  }
  console.log("만든json", result);
};

export { parcingExcel };
