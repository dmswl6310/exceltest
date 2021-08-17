package seegene.control.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap; 

import seegene.control.SgViewerControl;
import seegene.control.dye.SgDyeFactory;
import seegene.control.dye.SgDyeUndefinedException;
import seegene.control.testkit.SgTestKitFactory;
import seegene.data.SgSheetData;
import seegene.data.bean.SgControl;
import seegene.data.bean.SgResult;
import seegene.data.bean.SgResultInfo;
import seegene.data.bean.SgWellInfo;
import seegene.data.xml.SgXmlReader;
import seegene.utils.SgUtils;

/**
 * 개요 : Data Analyzer<br>
 * CFX96 결과 판독에 사용되는 데이터 모델을 설정합니다.<br><br>
 * 작성일 : Aug 17, 2012<br>
 * 작성자 : 민경현<br>
 * Version : 1.00
 */
public class SgDataAnalyzerCFX96 extends SgDataAnalyzer
{
	private SgSheetData [] qcData;
	private SgSheetData [] qaData;
	private SgSheetData [] qpvData;
	private SgSheetData [] qsData;
	private SgSheetData [] mcpvData;
	private SgSheetData [] mcpData;
	private SgSheetData [] mcdData;
	private SgSheetData [] mcaData;
	
	private String cfx96Version = "1.6";
	
	/**
	 * CFX96 Data Analyzer
	 * @param filePath
	 */
	public SgDataAnalyzerCFX96(String filePath, String instrument, SgSheetData nimbusData) 
	{
		super(filePath, instrument, nimbusData);
	}
	
	/**
	 * CFX96 Data Analyzer
	 * @param loadData
	 * @param instrument
	 */
	public SgDataAnalyzerCFX96(SgXmlReader loadData, String instrument) 
	{
		super(loadData, instrument);
	}

	/* (non-Javadoc)
	 * @see seegene.control.analyzer.DataAnalyzer#openDataFile(java.io.File)
	 */
	@Override
	public HashMap<String, SgWellInfo> openDataFile(String filePath)
	{
		try
		{
			// key=Well ID, value=Well Info
			HashMap<String, SgWellInfo> wellInfoMap = new HashMap<String, SgWellInfo>();

			// 분산된 데이터 파일 명을 설정합니다.
			String quantitationAmplification = changeSuffixInFileName(filePath, "  Quantitation Amplification Results.xlsx");
			String quantitationPlateView = changeSuffixInFileName(filePath, "  Quantitation Plate View Results.xlsx");
			String meltCurveAmplification = changeSuffixInFileName(filePath, "  Melt Curve Amplification Results.xlsx");
	        String meltCurveDerivative = changeSuffixInFileName(filePath, "  Melt Curve Derivative Results.xlsx");
	        String meltCurvePlateView = changeSuffixInFileName(filePath, "  Melt Curve Plate View Results.xlsx");
	        String meltCurvePeak = changeSuffixInFileName(filePath, "  Melt Curve Peak Results.xlsx");
	        String quantitationSummary = null;
	       
	        // CFX96 Manager Touch 장비 사용시 처리
//	        if(SgViewerControl.isBRL_ONLY || SgViewerControl.isCFX96Touch || SgViewerControl.isRUO)
	        {
		        if(filePath.indexOf("-  Quantification Cq Results.xlsx") != -1)
		        {
		        	cfx96Version = "3.0";
		        	quantitationAmplification = changeSuffixInFileName(filePath, "  Quantification Amplification Results.xlsx");
		        	quantitationPlateView = changeSuffixInFileName(filePath, "  Quantification Plate View Results.xlsx");
		        	quantitationSummary = changeSuffixInFileName(filePath, "  Quantification Summary.xlsx");
		        }
	        }
	        
	        // 각 파일을 Sheet Data 로 초기화 합니다.
	        qcData = null;
	        qaData = null;
	        qpvData = null;
	        mcpvData = null;
	        mcpData = null;
	        mcdData = null;
	        mcaData = null;
	        
	        if(new File(filePath).exists())
	        {
	        	if(!filePath.contains("-  Melt Curve Summary"))
	        	{
	        		qcData = SgUtils.readExcel(filePath);
	        	}
	        	else
	        	{
	        		String quantitationCt = changeSuffixInFileName(filePath, "  Quantification Cq Results.xlsx");
	        		if(new File(quantitationCt).exists())
	        		{
	        			qcData = SgUtils.readExcel(quantitationCt);
	        		}
	        	}
	        }
	        if(new File(quantitationAmplification).exists())
	        {
	        	qaData = SgUtils.readExcel(quantitationAmplification);
	        }
	        if(new File(quantitationPlateView).exists())
	        {
	        	qpvData = SgUtils.readExcel(quantitationPlateView);
	        }
	        if(quantitationSummary != null && new File(quantitationSummary).exists())
	        {
	        	qsData = SgUtils.readExcel(quantitationSummary);
	        }
	        if(new File(meltCurvePlateView).exists())
	        {
	        	mcpvData = SgUtils.readExcel(meltCurvePlateView);
	        }
	        if(new File(meltCurvePeak).exists())
	        {
	        	mcpData = SgUtils.readExcel(meltCurvePeak);
	        }
	        if(new File(meltCurveDerivative).exists())
	        {
	        	mcdData = SgUtils.readExcel(meltCurveDerivative);
	        }
	        if(new File(meltCurveAmplification).exists())
	        {
	        	mcaData = SgUtils.readExcel(meltCurveAmplification);
	        }
	        // Well Info 기본 데이터를 설정합니다.
	        if(qpvData != null)
        	{
        		initWellInfo(qpvData, wellInfoMap);
        	}
	        // Melt Peak 데이터를 설정합니다.
	        if(wellInfoMap.size() == 0)
	        {
	        	if(mcpvData != null)
	        	{
	        		initMeltPeakInfo(mcpvData, wellInfoMap);
	        		initWellName(mcpData, wellInfoMap);
	        	}
	        }
	        // CT 데이터를 설정합니다.
	        if(qcData != null)
	        {
	        	initCtInfo(qcData, wellInfoMap);
	        }
	        if(qsData != null)
	        {
	        	initRunInfo(qsData);
	        }
	        // Cycle Value 데이터를 설정합니다.
	        if(qaData != null)
	        {
	        	initCycleInfo(qaData, wellInfoMap);
	        }
	        // Temperature Value 데이터를 설정합니다.
	        if(mcdData != null)
	        {
		        initTemperatureInfo(mcdData, wellInfoMap);
	        }
	        if(mcaData != null)
	        {
	        	initMeltCurveInfo(mcaData, wellInfoMap);
	        }
	        return wellInfoMap;
		}
		catch(SgDyeUndefinedException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * CFX-96 데이터 파일 명의 접미사를 설정해 반환합니다.
	 * @param fileName 메인 파일 명
	 * @param suffix 해당 데이터 파일 명의 접미사
	 * @return
	 */
	private static String changeSuffixInFileName(String fileName, String suffix)
    {
		String suffixFileName = "";
		if (fileName.contains("-  Quantitation Ct Results.xlsx") || fileName.contains("-  Quantification Cq Results.xlsx") || fileName.contains("-  Melt Curve Summary"))
        {
	        String [] prefixFileName = fileName.split("-");
	        for (int i = 0; i < prefixFileName.length - 1; i++)
	        {
	        	suffixFileName += prefixFileName[i] + "-";
	        }
        	suffixFileName += suffix;
        }
        return suffixFileName;
    }
	
	/**
	 * Quantitation 데이터 기준으로 Well Info 를 초기화 합니다.
	 * @param qpvData
	 * @param wellInfoMap
	 * @throws SgDyeUndefinedException 
	 */
	private void initWellInfo(SgSheetData [] qpvData, HashMap<String, SgWellInfo> wellInfoMap) throws SgDyeUndefinedException
	{
		ArrayList<String> dyes = new ArrayList<String>();
		// 데이터 입력 전에 각 탭의 dye 정보 먼저 확인합니다.
		for (SgSheetData sheetData : qpvData)
		{
			// Tab 이름으로 dye 을 설정합니다. 
			dyes.add(sheetData.getSheetName());
		}
		// 3.0부터 존재하는 run information 탭을 삭제합니다.
		if(cfx96Version != null && cfx96Version.equals("3.0"))
		{
			dyes.remove("Run Information");
		}
		for (SgSheetData sheetData : qpvData)
		{
			// Plate 에 표시된 Well 정보를 설정합니다.
			for(int i=1; i<sheetData.getRowCount(); i=i+4)
	        {
				// Row 별로 데이터의 column 이 다르기 때문에 전체 ColumnCount 를 사용하면 오류가 발생합니다.
				// 각 Row 별로 Column 길이를 직접 가져와서 적용했습니다.
				// for(int j=2; j<qpvData[0].getColumnCount(); j++)
				for(int j=2; j<sheetData.getData()[i].length; j++)
				{
					if(sheetData.getData(i, j) != null)
					{
						// 공란으로 되어 있는 경우 사용자가 오해 하지 않도록 예외처리 해줍니다.
						if(!String.valueOf(sheetData.getData(i, j)).trim().equals(""))
						{
							String wellId = String.valueOf(sheetData.getData(i, 0)) + String.valueOf(sheetData.getData(0, j));
							wellId = SgUtils.wellId(wellId);
							String wellName = "";
							String wellType = getWellControlType(String.valueOf(sheetData.getData(i, j)));
							String standardType = SgControl.getStandardType(String.valueOf(sheetData.getData(i, j)));

							// Well Name 정보도 row 별로 column 길이를 직접 가져와서 예외처리해 줍니다.
							if(sheetData.getData()[i+1].length > j && sheetData.getData(i+1, j) != null)
							{
								wellName = String.valueOf(sheetData.getData(i+1, j));
							}
							// Well 데이터를 초기화합니다.
				        	if(!wellInfoMap.containsKey(wellId))
				        	{
				        		SgWellInfo wellInfo = new SgWellInfo();
				        		wellInfo.setAnalyzer(this);
				        		wellInfo.setSemiQAnalyzers(getSemiQAnalyzers());
				        		wellInfo.setWellId(wellId);
				        		wellInfo.setWellName(wellName);
				        		wellInfo.setWellType(wellType);
				        		wellInfo.setStandardType(standardType);
				        		wellInfo.setPrintable(false);
				        		
				        		// dye 정보를 입력합니다.
				        		ArrayList<SgResultInfo> resultInfoList = new ArrayList<SgResultInfo>();
				        		for(String dye : dyes)
				        		{
				        			if(SgDyeFactory.getChannel(dye) != null)
				        			{
				        				resultInfoList.add(new SgResultInfo(wellInfo, dye));
				        			}
				        		}
				        		wellInfo.setResultInfos(resultInfoList);
				        		wellInfoMap.put(wellId, wellInfo);
				        	}
						}
					}
				}
	        }
		}
		initNimbusSetting(wellInfoMap);
		setDyes(dyes);
	}
	
	/**
	 * CFX96 Manager 3.1 버전일 경우 실행 정보를 가져옵니다.
	 * @param qsData
	 */
	private void initRunInfo(SgSheetData [] qsData)
	{
		if(qsData.length > 1 && qsData[1] != null && qsData[1].getSheetName().equals("Run Information"))
		{
			for(int row=0; row < qsData[1].getRowCount(); row++)
			{
				if(qsData[1].getColumnCount() > 0)
				{
					if(qsData[1].getData(row, 0).equals("Run Ended"))
					{
						setRunDate(String.valueOf(qsData[1].getData(row, 1)));
					}
					else if(qsData[1].getData(row, 0).equals("Base Serial Number"))
					{
						setRunSerialNumber(String.valueOf(qsData[1].getData(row, 1)));
					}
				}
			}
		}
	}
	
	/**
	 * Well Info 데이터를 설정합니다.
	 * @param qcData
	 * @param wellInfoMap
	 */
	private void initCtInfo(SgSheetData [] qcData, HashMap<String, SgWellInfo> wellInfoMap)
	{
		for(int i=1; i<qcData[0].getRowCount(); i++)
        {
			// Well 기본 정보를 설정합니다.
        	String wellId = String.valueOf(qcData[0].getData(i, 1));
        	String dye = String.valueOf(qcData[0].getData(i, 2));
        	String targetName = String.valueOf(qcData[0].getData(i, 4));
        	String wellName = String.valueOf(qcData[0].getData(i, 5));
        	
        	Double ct = null;
        	Double sq = null;
        	Double logSq = null;
        	if(cfx96Version != null && cfx96Version.equals("3.0"))
        	{
        		if(qcData[0].getData(i, 8) != null && !qcData[0].getData(i, 8).toString().isEmpty())
	    		{
	        		try
	        		{
	        			targetName = String.valueOf(qcData[0].getData(i, 4));
	        		}
	        		catch(Exception e){}
	    		}
        		if(qcData[0].getData(i, 8) != null && !qcData[0].getData(i, 8).toString().isEmpty())
	    		{
	        		try
	        		{
		        		ct = Double.valueOf(String.valueOf(qcData[0].getData(i, 8)));
		        		/**
		        		 * 2019-10-30 wwjoo
		        		 * 정량제품 조합검사 기능 추가
		        		 */
		        		if(getIsCombinedSD())
		        		{
		        			if(wellInfoMap.containsKey(wellId))
		        			{
		        				String testKitName = wellInfoMap.get(wellId).getPanelInfo().getName();
		        				setMaxCt(testKitName,ct);
		        				setMinCt(testKitName,ct);
		        			}
		        			else
		        			{
				        		setMaxCt(ct);
				        		setMinCt(ct);		        				
		        			}
		        		}
		        		else
		        		{
			        		setMaxCt(ct);
			        		setMinCt(ct);		        			
		        		}
	        		}
	        		catch(Exception e){}
	    		}
	        	if(qcData[0].getData(i, 10) != null && !qcData[0].getData(i, 10).toString().isEmpty())
	    		{
	        		try
	        		{
	        			sq = Double.valueOf(String.valueOf(qcData[0].getData(i, 10)));
	        		}
	        		catch(Exception e){}
	    		}
	        	if(qcData[0].getData(i, 11) != null && !qcData[0].getData(i, 11).toString().isEmpty())
	    		{
	        		try
	        		{
	        			logSq = Double.valueOf(String.valueOf(qcData[0].getData(i, 11)));
	        		}
	        		catch(Exception e){}
	    		}
        	}
        	else
        	{
	        	if(qcData[0].getData(i, 6) != null && !qcData[0].getData(i, 6).toString().isEmpty())
	    		{
	        		try
	        		{
		        		ct = Double.valueOf(String.valueOf(qcData[0].getData(i, 6)));
		        		/**
		        		 * 2019-10-30 wwjoo
		        		 * 정량제품 조합검사 기능 추가
		        		 */
		        		if(getIsCombinedSD())
		        		{
		        			if(wellInfoMap.containsKey(wellId))
		        			{
		        				String testKitName = wellInfoMap.get(wellId).getPanelInfo().getName();
		        				setMaxCt(testKitName,ct);
		        				setMinCt(testKitName,ct);
		        			}
		        			else
		        			{
				        		setMaxCt(ct);
				        		setMinCt(ct);		        				
		        			}
		        		}
		        		else
		        		{
			        		setMaxCt(ct);
			        		setMinCt(ct);		        			
		        		}
	        		}
	        		catch(Exception e){}
	    		}
	        	if(qcData[0].getData(i, 9) != null && !qcData[0].getData(i, 9).toString().isEmpty())
	    		{
	        		try
	        		{
	        			sq = Double.valueOf(String.valueOf(qcData[0].getData(i, 9)));
	        		}
	        		catch(Exception e){}
	    		}
	        	if(qcData[0].getData(i, 10) != null && !qcData[0].getData(i, 10).toString().isEmpty())
	    		{
	        		try
	        		{
	        			logSq = Double.valueOf(String.valueOf(qcData[0].getData(i, 10)));
	        		}
	        		catch(Exception e){}
	    		}
        	}

        	// 데이터 초기화.
        	if(wellInfoMap.containsKey(wellId))
        	{
        		SgWellInfo wellInfo = wellInfoMap.get(wellId);
        		if(nimbusData == null)
        		{
        			wellInfo.setWellName(wellName);
        		}
        		if(wellInfo.getResultInfo(dye) != null)
        		{
        			wellInfo.getResultInfo(dye).setCt(ct);
        			wellInfo.getResultInfo(dye).setSq(sq);
        			wellInfo.getResultInfo(dye).setLogSq(logSq);
        		}
        		if(targetName.contains("urine"))
        		{
        			wellInfo.setUrine(true);
        		}
        	}
        }
	}
	
	/**
	 * Well Info 데이터를 설정합니다.
	 * @param qcData
	 * @param wellInfoMap
	 * @throws SgDyeUndefinedException 
	 */
	private void initMeltPeakInfo(SgSheetData [] mcpvData, HashMap<String, SgWellInfo> wellInfoMap) throws SgDyeUndefinedException
	{
		ArrayList<String> dyes = new ArrayList<String>();
		// 데이터 입력 전에 각 탭의 dye 정보 먼저 확인합니다.
		if(qpvData != null)
		{
			for (SgSheetData sheetData : qpvData)
			{
				// Tab 이름으로 dye 을 설정합니다. 
				dyes.add(sheetData.getSheetName());
			}
		}
		else
		{
			for (SgSheetData sheetData : mcpvData)
			{
				// Tab 이름으로 dye 을 설정합니다. 
				dyes.add(sheetData.getSheetName());
			}
		}
		for (SgSheetData sheetData : mcpvData)
		{
			// Plate 에 표시된 Well 정보를 설정합니다.
			for(int i=1; i<sheetData.getRowCount(); i=i+4)
	        {
				// Row 별로 데이터의 column 이 다르기 때문에 전체 ColumnCount 를 사용하면 오류가 발생합니다.
				// 각 Row 별로 Column 길이를 직접 가져와서 적용했습니다.
				// for(int j=2; j<qpvData[0].getColumnCount(); j++)
				for(int j=2; j<sheetData.getData()[i].length; j++)
				{
					if(sheetData.getData(i, j) != null)
					{
						// 공란으로 되어 있는 경우 사용자가 오해 하지 않도록 예외처리 해줍니다.
						if(!String.valueOf(sheetData.getData(i, j)).trim().equals(""))
						{
							String wellId = String.valueOf(sheetData.getData(i, 0)) + String.valueOf(sheetData.getData(0, j));
							wellId = SgUtils.wellId(wellId);
							String wellName = "";
							String wellType = getWellControlType(String.valueOf(sheetData.getData(i, j)));
							String standardType = SgControl.getStandardType(String.valueOf(sheetData.getData(i, j)));
							// Well Name 정보도 row 별로 column 길이를 직접 가져와서 예외처리해 줍니다.
							if(sheetData.getData()[i+1].length > j && sheetData.getData(i+1, j) != null)
							{
								wellName = String.valueOf(sheetData.getData(i+1, j));
							}
							// Well 데이터를 초기화합니다.
				        	if(!wellInfoMap.containsKey(wellId) && wellType != null)
				        	{
				        		SgWellInfo wellInfo = new SgWellInfo();
				        		wellInfo.setWellName(wellName);
				        		wellInfo.setAnalyzer(this);
				        		wellInfo.setSemiQAnalyzers(getSemiQAnalyzers());
				        		wellInfo.setWellId(wellId);
				        		wellInfo.setWellType(wellType);
				        		wellInfo.setStandardType(standardType);
				        		wellInfo.setPrintable(false);
				        		
				        		// dye 정보를 입력합니다.
				        		ArrayList<SgResultInfo> resultInfoList = new ArrayList<SgResultInfo>();
				        		for(String dye : dyes)
				        		{
				        			if(SgDyeFactory.getChannel(dye) != null)
				        			{
				        				resultInfoList.add(new SgResultInfo(wellInfo, dye));
				        			}
				        		}
				        		wellInfo.setResultInfos(resultInfoList);
				        		
				        		wellInfoMap.put(wellId, wellInfo);
				        	}
						}
					}
				}
	        }
		}
		initNimbusSetting(wellInfoMap);
		setDyes(dyes);
	}
	
	/**
	 * Melt 데이터에서 Well Name 을 설정합니다.<br>
	 * Quantitation Plate 에 데이터가 있는 경우는 실행하지 않습니다.
	 * @param mcpData
	 * @param wellInfoMap
	 */
	private void initWellName(SgSheetData [] mcpData, HashMap<String, SgWellInfo> wellInfoMap)
	{
		for(int i=1; i<mcpData[0].getRowCount(); i=i+4)
        {
			String wellId = String.valueOf(mcpData[0].getData(0, 1));
			wellId = SgUtils.wellId(wellId);
			String wellName = String.valueOf(mcpData[0].getData(0, 5));
			if(wellInfoMap.containsKey(wellId) && nimbusData == null)
			{
				wellInfoMap.get(wellId).setWellName(wellName);
			}
        }
	}
	
	/**
	 * Temperature 데이터를 설정합니다.
	 * @param mcdData
	 * @param wellInfoMap
	 */
	private void initTemperatureInfo(SgSheetData [] mcdData, HashMap<String, SgWellInfo> wellInfoMap)
	{
		initResultInfo(mcdData, wellInfoMap, TEMPERATURE_TYPE);
	}
	
	/**
	 * Cycle 데이터를 설정합니다.
	 * @param qaData
	 * @param wellInfoMap
	 */
	private void initCycleInfo(SgSheetData [] qaData, HashMap<String, SgWellInfo> wellInfoMap)
	{
		initResultInfo(qaData, wellInfoMap, CYCLE_TYPE);
	}
	
	/**
	 * Melt Curve 데이터를 설정합니다.
	 * @param mcaData
	 * @param wellInfoMap
	 */
	private void initMeltCurveInfo(SgSheetData [] mcaData, HashMap<String, SgWellInfo> wellInfoMap)
	{
		for (SgSheetData sheetData : mcaData)
		{
			// Tab 이름으로 dye 을 설정합니다. 
			String dye = sheetData.getSheetName();
			// Sheet 의 데이터를 가져옵니다.
			Object datas[][] = sheetData.getData();
			
			// 실제 Well Data 는 3번째 컬럼부터 저장되어 있습니다.
			for(int column=2; column<datas[0].length; column++)
			{
				// Export 데이터마다 Well ID 기준이 다릅니다. 2자리로 표현된 Well ID는 3자리로 표준화 합니다.
				String wellId = String.valueOf(datas[0][column]);
				wellId = SgUtils.wellId(wellId);
				if(wellId != null && wellInfoMap.containsKey(wellId))
				{
					// Cycle, Temperature 정보를 기준으로 result 를 저장할 리스트를 생성합니다.
					ArrayList<SgResult> meltCurveResults = new ArrayList<SgResult>();
					
					SgResultInfo resultInfo = wellInfoMap.get(wellId).getResultInfo(dye);
					// Temperature 기준으로 기본 Result 객체를 생성해 줍니다.
					for(int row=1; row<datas.length; row++)
					{
						SgResult result = new SgResult(resultInfo);
						result.setTemperature((Double)datas[row][1]);
						// Temperature 기준으로 생성한 기본 Result 객체에 결과값을 설정합니다.
						result.setValue((Double)datas[row][column]);
						meltCurveResults.add(result);
					}
					if(resultInfo != null)
					{
						resultInfo.setMeltCurveResults(meltCurveResults);
					}
				}
			}
		}
	}
	
	/**
	 * Temperature, Cycle 데이터를 구분하여 설정합니다.
	 * @param data
	 * @param wellInfoMap
	 * @param type
	 */
	private void initResultInfo(SgSheetData [] data, HashMap<String, SgWellInfo> wellInfoMap, int type)
	{
		for (SgSheetData sheetData : data)
		{
			// Tab 이름으로 dye 을 설정합니다. 
			String dye = sheetData.getSheetName();
			// Sheet 의 데이터를 가져옵니다.
			Object datas[][] = sheetData.getData();
			
			// 실제 Well Data 는 3번째 컬럼부터 저장되어 있습니다.
			for(int column=2; column<datas[0].length; column++)
			{
				// Export 데이터마다 Well ID 기준이 다릅니다. 2자리로 표현된 Well ID는 3자리로 표준화 합니다.
				String wellId = String.valueOf(datas[0][column]);
				wellId = SgUtils.wellId(wellId);
				if(wellId != null && wellInfoMap.containsKey(wellId))
				{
					// Cycle, Temperature 정보를 기준으로 result 를 저장할 리스트를 생성합니다.
					ArrayList<SgResult> resultList = new ArrayList<SgResult>();
					
					// Cycle 기준과 Temperature 기준으로 구분하여 설정합니다.
					if(type == CYCLE_TYPE)
					{
						SgResultInfo resultInfo = wellInfoMap.get(wellId).getResultInfo(dye);
						// Cycle 기준으로 기본 Result 객체를 생성해 줍니다.
						for(int row=1; row<datas.length; row++)
						{
							SgResult result = new SgResult(resultInfo);
							result.setCycle((Double)datas[row][1]);
							// Cycle 기준으로 생성한 기본 Result 객체에 결과값을 설정합니다.
							result.setValue((Double)datas[row][column]);
							resultList.add(result);
							// Cycle 기준 데이터 중 제일 작은 Step 과 큰 Step 을 설정합니다.
							setMinMaxCycleStep((Double)datas[row][1]);
							// Cycle 기준 데이터 중 제일 작은 값과 큰 값을 설정합니다.
							setMinMaxCycleValue((Double)datas[row][column]);
//							if((Double)datas[row][1] <= 40)
//							{
//								SgResult result = new SgResult(resultInfo);
//								result.setCycle((Double)datas[row][1]);
//								// Cycle 기준으로 생성한 기본 Result 객체에 결과값을 설정합니다.
//								result.setValue((Double)datas[row][column]);
//								resultList.add(result);
//								// Cycle 기준 데이터 중 제일 작은 Step 과 큰 Step 을 설정합니다.
//								setMinMaxCycleStep((Double)datas[row][1]);
//								// Cycle 기준 데이터 중 제일 작은 값과 큰 값을 설정합니다.
//								setMinMaxCycleValue((Double)datas[row][column]);
//							}
						}
						if(resultInfo != null)
						{
							resultInfo.setCycleResults(resultList);
						}
					}
					else if(type == TEMPERATURE_TYPE)
					{
						SgResultInfo resultInfo = wellInfoMap.get(wellId).getResultInfo(dye);
						// Temperature 기준으로 기본 Result 객체를 생성해 줍니다.
						for(int row=1; row<datas.length; row++)
						{
							SgResult result = new SgResult(resultInfo);
							result.setTemperature((Double)datas[row][1]);
							// Temperature 기준으로 생성한 기본 Result 객체에 결과값을 설정합니다.
							result.setValue((Double)datas[row][column]);
							resultList.add(result);
							// Temperature 기준 데이터 중 제일 작은 Step 과 큰 Step 을 설정합니다.
							setMinMaxTempStep((Double)datas[row][1]);
							// Temperature 기준 데이터 중 제일 작은 값과 큰 값을 설정합니다.
							setMinMaxTempValue((Double)datas[row][column]);
						}
						if(resultInfo != null)
						{
							resultInfo.setTempResults(resultList);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Wild Type Control 을 초기화 합니다.
	 * type : MWTC, XWTC
	 */
	private void initWTC()
	{
		try
		{
			setMwtcMap(new HashMap<String, Double>());
			setXwtcMap(new HashMap<String, Double>());
			// wild type control 정보를 연결해 줍니다.
			for(SgWellInfo wellInfo : getWellInfoMap().values())
			{
				getWellInfoMap().get(wellInfo.getWellId()).setMwtcMap(getMwtcMap());
				getWellInfoMap().get(wellInfo.getWellId()).setXwtcMap(getXwtcMap());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Wild Type Control 을 설정합니다.
	 */
	public void importWTC()
	{
		// 데이터를 입력하기 전에 Wild Type Control 을 초기화 해줍니다.
		initWTC();
		for(SgWellInfo wellInfo : getWellInfoMap().values())
		{
			if(SgControl.isMWTC(wellInfo) || SgControl.isMWTCe(wellInfo))
			{
				for(SgResultInfo resultInfo : wellInfo.getResultInfos())
				{
					// TEMP 데이터에서만 처리합니다.
					if(resultInfo.getTempResults() != null && resultInfo.getTempResults().size() > 0)
					{
						double start = 0;
						double end = 0;
						// TestKit 적용 전에 선처리를 하기 때문에 각 채널별로 코드에 직접 Tm Range 입력
						if(resultInfo.getDyeInfo().getChannel().equals("2"))
						{
							start = 62;
							end = 70;
						}
						else if(resultInfo.getDyeInfo().getChannel().equals("3"))
						{
							start = 62;
							end = 72;
						}
						for(SgResult result : resultInfo.getTempResults())
						{
							// importMWTC(result, resultInfo.getDyeInfo().getDye());
							// 60~80도 사이의 값 중에서 설정
							// 고권혜 대리 2014.7.7
							/*
							if(result.getTemperature() > 60 && result.getTemperature() < 80)
							{
								importMWTC(result, resultInfo.getDyeInfo().getDye());
							}
							*/
							// 한지승 대리 요청으로 해당 Target Tm Range 로 처리
							if(result.getTemperature() >= start && result.getTemperature() <= end)
							{
								importMWTC(result, resultInfo.getDyeInfo().getDye());
							}
						}
					}
				}
			}
			if(SgControl.isXWTC(wellInfo) || SgControl.isXWTCe(wellInfo))
			{
				for(SgResultInfo resultInfo : wellInfo.getResultInfos())
				{
					if(resultInfo.getTempResults() != null && resultInfo.getTempResults().size() > 0)
					{
						double start = 0;
						double end = 0;
						// TestKit 적용 전에 선처리를 하기 때문에 각 채널별로 코드에 직접 Tm Range 입력
						if(resultInfo.getDyeInfo().getChannel().equals("2"))
						{
							start = 62.5;
							end = 67;
						}
						else if(resultInfo.getDyeInfo().getChannel().equals("3"))
						{
							start = 63.5;
							end = 78.5;
						}
						for(SgResult result : resultInfo.getTempResults())
						{
							// importXWTC(result, resultInfo.getDyeInfo().getDye());
							// 60~80도 사이의 값 중에서 설정
							// 고권혜 대리 2014.7.7
							/*
							if(result.getTemperature() > 60 && result.getTemperature() < 80)
							{
								importXWTC(result, resultInfo.getDyeInfo().getDye());
							}
							*/
							// 한지승 대리 요청으로 해당 Target Tm Range 로 처리
							if(result.getTemperature() >= start && result.getTemperature() <= end)
							{
								importXWTC(result, resultInfo.getDyeInfo().getDye());
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * MWTC 을 설정합니다.
	 * @param result
	 * @param dye
	 */
	private void importMWTC(SgResult result, String dye)
	{
		if(getMwtcMap().containsKey(SgDyeFactory.getChannel(dye)))
		{
			if(getMwtcMap().get(SgDyeFactory.getChannel(dye)) < result.getValue())
			{
				getMwtcMap().put(SgDyeFactory.getChannel(dye), result.getValue());
			}
		}
		else
		{
			getMwtcMap().put(SgDyeFactory.getChannel(dye), result.getValue());
		}
	}
	
	/**
	 * XWTC 을 설정합니다.
	 * @param result
	 * @param dye
	 */
	private void importXWTC(SgResult result, String dye)
	{
		if(getXwtcMap().containsKey(SgDyeFactory.getChannel(dye)))
		{
			if(getXwtcMap().get(SgDyeFactory.getChannel(dye)) < result.getValue())
			{
				getXwtcMap().put(SgDyeFactory.getChannel(dye), result.getValue());
			}
		}
		else
		{
			getXwtcMap().put(SgDyeFactory.getChannel(dye), result.getValue());
		}
	}
	
	private void initNimbusSetting(HashMap<String, SgWellInfo> wellInfoMap)
	{
		// LIS 데이터에서 제품 코드 설정
		if(nimbusData != null)
		{
			HashMap<String, SgWellInfo> checkWell = new HashMap<String, SgWellInfo>();
			HashMap<String, String> plateTypeMap = new HashMap<String, String>();
			HashMap<String, String> capFilmMap = new HashMap<String, String>();
			for(SgWellInfo wellInfo : wellInfoMap.values())
			{
				String wellName = wellInfo.getWellName();
				String wellType =wellInfo.getWellType();
				String sampleNo = null;
				String patientId = null;
				String testKitId = "";
				String panelId = "";
				boolean checkedNimbus = false;
				
				for(int row=0; row<nimbusData.getRowCount(); row++)
				{
					String nimbusWellId = nimbusData.getDataString(row, "ROW") + nimbusData.getDataString(row, "COLUMN");
					nimbusWellId = SgUtils.wellId(nimbusWellId);
					String nimbusTestKitPanelId = null;
					if(nimbusData.getData(row, "*TARGET NAME") != null && nimbusData.getDataString(row, "*TARGET NAME").split("\\|").length == 2)
					{
						nimbusTestKitPanelId = nimbusData.getDataString(row, "*TARGET NAME");
					}
					if(wellInfo.getWellId().equals(nimbusWellId) && nimbusData.getData(row, "*SAMPLE NAME") != null)
					{
						checkedNimbus = true;
						wellName = String.valueOf(nimbusData.getData(row, "*SAMPLE NAME"));
						String tempWellName = wellName.trim().replaceAll(" ", "" );
						if(wellName.toUpperCase().indexOf("PC") != -1 && ( 
								tempWellName.equalsIgnoreCase( "PC" ) || 
								tempWellName.equalsIgnoreCase( "PC1" ) || 
								tempWellName.equalsIgnoreCase( "PC2" ) || 
								tempWellName.equalsIgnoreCase( "PC3" ) ) )
						{
							wellType = SgControl.POSITIVE_CONTROL;
						}
						else if(wellName.toUpperCase().indexOf("NC") != -1 && tempWellName.equalsIgnoreCase( "NC" ) )
						{
							wellType = SgControl.NEGATIVE_CONTROL;
						}
						else if(wellName.toUpperCase().contains("STD") && (
								tempWellName.equalsIgnoreCase( "STD" ) || 
								tempWellName.equalsIgnoreCase( "STD-1" ) || 
								tempWellName.equalsIgnoreCase( "STD-2" ) || 
								tempWellName.equalsIgnoreCase( "STD-3" ) || 
								tempWellName.equalsIgnoreCase( "STD-4" ) || 
								tempWellName.equalsIgnoreCase( "STD-5" ) || 
								tempWellName.equalsIgnoreCase( "STD-6" ) || 
								tempWellName.equalsIgnoreCase( "STD1" ) || 
								tempWellName.equalsIgnoreCase( "STD2" ) || 
								tempWellName.equalsIgnoreCase( "STD3" ) || 
								tempWellName.equalsIgnoreCase( "STD4" ) || 
								tempWellName.equalsIgnoreCase( "STD5" ) || 
								tempWellName.equalsIgnoreCase( "STD6" ) || 
								tempWellName.equalsIgnoreCase( "STANDARD" ) ) )
						{
							wellType = SgControl.STANDARD;
						}
						else if(!wellName.trim().equals(""))
						{
							wellType = SgControl.SAMPLE;
						}
						else
						{
							wellType = null;
						}
						if(nimbusData.getData(row, "SAMPLE NO") != null)
						{
							sampleNo = nimbusData.getDataString(row, "SAMPLE NO");
						}
						if(nimbusData.getData(row, "PATIENT ID") != null)
						{
							patientId = nimbusData.getDataString(row, "PATIENT ID");
						}
						if(nimbusData.getData(row, "PATIENT NAME") != null)
						{
							patientId = nimbusData.getDataString(row, "PATIENT NAME");
						}
						if(nimbusTestKitPanelId != null)
						{
							testKitId = nimbusTestKitPanelId.split("\\|")[0];
							panelId = nimbusTestKitPanelId.split("\\|")[1];
						}
						if(nimbusData.getData(row, "PRODUCT NAME") != null)
						{
							String productAbb = nimbusData.getDataString(row, "PRODUCT NAME");
							String plateType = nimbusData.getDataString(row, "PLATE TYPE");
							String capFilm = nimbusData.getDataString(row, "CAP FILM");
							String extraction = nimbusData.getDataString(row, "Extraction Type"); // 훈석 CRUDE_EXTRACTION
							
							//2020-09-23 wwjoo Launcher 버전 추가	STAT_UPLOAD
							String launcherVersion = nimbusData.getDataString(row, "Launcher");
							setLauncherVersion(launcherVersion);
							
							plateTypeMap.put(wellInfo.getWellId(), plateType);
							capFilmMap.put(wellInfo.getWellId(), capFilm);
							int panelIdIndex = Integer.valueOf(nimbusData.getDataString(row, "PANEL INDEX"));
							
							testKitId = SgTestKitFactory.getTestKitId(productAbb, plateType, capFilm, extraction); // 훈석 CRUDE_EXTRACTION
							
							if(testKitId != null)
							{
								panelId = SgTestKitFactory.getTestKitInfo(testKitId).getPanelInfos().get(panelIdIndex).getId();
							}
						}
						if(nimbusData.getData(row, "User Name") != null)
						{
							setNimbusStarletOperator(nimbusData.getDataString(row, "User Name"));
						}
						if(nimbusData.getData(row, "Extraction Barcode") != null)
						{
							wellInfo.setExtractionBarcode(nimbusData.getDataString(row, "Extraction Barcode"));
						}
						if(nimbusData.getData(row, "PCR Barcode") != null)
						{
							wellInfo.setPcrBarcode(nimbusData.getDataString(row, "PCR Barcode"));
						}
						if(nimbusData.getDataString(row, "CLOT SAMPLE") != null && nimbusData.getDataString(row, "CLOT SAMPLE").equals("true"))
						{
							wellInfo.setClotSample(true);
						}
						if(nimbusData.getDataString(row, "CH1 TARGET NAME") != null && nimbusData.getDataString(row, "CH1 TARGET NAME").equals("urine"))
						{
							wellInfo.setUrine(true);
						}
						if(nimbusData.getData(row, "DWP Barcode") != null)
						{
							wellInfo.setDwpBarcode(nimbusData.getDataString(row, "DWP Barcode"));
						}
						if(nimbusData.getData(row, "Plate Id") != null)
						{
							wellInfo.setPlateBarcode(nimbusData.getDataString(row, "Plate Id"));
						}
					}
				}
				if(!checkedNimbus)
				{
					wellType = null;
				}
				if(testKitId != null && panelId != null && SgTestKitFactory.getTestKitInfo(testKitId) != null && SgTestKitFactory.getTestKitInfo(testKitId).getPanelInfo(panelId) != null)
				{
					wellInfo.setPanelInfo(SgTestKitFactory.getTestKitInfo(testKitId).getPanelInfo(panelId));
				}
	    		if(wellType != null && SgControl.SAMPLE.equals(wellType))
	    		{
	    			if(nimbusData.getColumnIndex("PATIENT ID") != -1 || nimbusData.getColumnIndex("PATIENT NAME") != -1)
	    			{
	    				String plrnBarcode = SgViewerControl.getPlrnBarcode();
	    				String plrnName = SgViewerControl.getPlrnName();
	    				if(plrnBarcode != null && plrnName != null)
	    				{
		    				if(plrnBarcode.equals("Patient Id"))
		    				{
		    					wellInfo.setPatientId(wellName);
		    				}
		    				if(plrnBarcode.equals("Sample No"))
		    				{
		    					wellInfo.setSampleNo(wellName);
		    				}
		    				if(plrnBarcode.equals("Well Name"))
		    				{
		    					wellInfo.setWellName(wellName);
		    				}
		    				if(plrnName.equals("Patient Id"))
		    				{
		    					wellInfo.setPatientId(patientId);
		    				}
		    				if(plrnName.equals("Sample No"))
		    				{
		    					wellInfo.setSampleNo(patientId);
		    				}
		    				if(plrnName.equals("Well Name"))
		    				{
		    					wellInfo.setWellName(patientId);
		    				}
	    				}
	    				else
	    				{
		    				wellInfo.setWellName(wellName);
		    				wellInfo.setSampleNo(sampleNo);
		    				wellInfo.setPatientId(patientId);
	    				}
	    			}
	    			else
	    			{
	    				if(SgViewerControl.getUseNimbusSetting() != null)
	    				{
		        			if(wellName.toUpperCase().equals("WTC"))
		        			{
		        				wellInfo.setWellName(wellName);
		        			}
		        			else if(SgViewerControl.getUseNimbusSetting().toUpperCase().equals("SAMPLE NO"))
		        			{
		        				wellInfo.setSampleNo(wellName);
		        			}
		        			else if(SgViewerControl.getUseNimbusSetting().toUpperCase().equals("PATIENT ID"))
		        			{
		        				wellInfo.setPatientId(wellName);
		        			}
		        			else if(SgViewerControl.getUseNimbusSetting().toUpperCase().equals("NAME"))
		        			{
		        				wellInfo.setWellName(wellName);
		        			}
		        			else if(SgViewerControl.getUseNimbusSetting().toUpperCase().equals("TRUE"))
		        			{
		        				wellInfo.setWellName(wellName);
		        			}
	    				}
	    			}
	    		}
	    		else
	    		{
	    			wellInfo.setWellName(wellName);
	    		}
	    		if(wellType != null)
	    		{
	    			wellInfo.setWellType(wellType);
	    		}
	    		else
	    		{
	    			checkWell.put(wellInfo.getWellId(), wellInfo);
	    		}
			}
			
			HashMap<String, String> testKitIdMap = new HashMap<String, String>();
			for(SgWellInfo wellInfo : wellInfoMap.values())
			{
				if(wellInfo.getPanelInfo() != null && !testKitIdMap.containsKey(wellInfo.getPanelInfo().getTestKitInfo().getId()))
				{
					String testKitId = wellInfo.getPanelInfo().getTestKitInfo().getId();
					String plateType = plateTypeMap.get(wellInfo.getWellId());
					String capFilm = capFilmMap.get(wellInfo.getWellId());
					if(testKitId.equals("RTDS000002V1"))
					{
						if(plateType.toUpperCase().equals("STRIP"))
						{
							testKitId = "RTDS000058V1";
						}
						if(plateType.toUpperCase().equals("PLATE"))
						{
							if(capFilm.toUpperCase().equals("CAP"))
							{
								testKitId = "RTDS000058V2";
							}
							if(capFilm.toUpperCase().equals("FILM"))
							{
								testKitId = "RTDS000058V3";
							}
						}
					}
					else if(testKitId.equals("RTDS000057V1"))
					{
						if(plateType.toUpperCase().contains("STRIP"))
						{
							testKitId = "RTDS000024V2";
						}
						else if(plateType.toUpperCase().contains("PLATE"))
						{
							if(capFilm.toUpperCase().equals("CAP"))
							{
								testKitId = "RTDS000037V2";
							}
							if(capFilm.toUpperCase().equals("FILM"))
							{
								testKitId = "RTDS000042V1";
							}
						}
					}
					else if(testKitId.equals("RTDS000055V1"))
					{
						if(plateType.toUpperCase().contains("STRIP"))
						{
							testKitId = "RTDS000024V2";
						}
						else if(plateType.toUpperCase().contains("PLATE"))
						{
							if(capFilm.toUpperCase().equals("CAP"))
							{
								testKitId = "RTDS000037V2";
							}
							if(capFilm.toUpperCase().equals("FILM"))
							{
								testKitId = "RTDS000042V1";
							}
						}
					}
					else if(SgTestKitFactory.getFullPanelTestKitId(wellInfo.getPanelInfo().getTestKitInfo().getId()) != null)
					{
						testKitId = SgTestKitFactory.getFullPanelTestKitId(wellInfo.getPanelInfo().getTestKitInfo().getId());
						//2020-04-12 wwjoo plrn 설정 시 TestKit 분리 오류 수정
						if(testKitId.equals("RTDS000062V1") ||testKitId.equals("RTDS000063V1") ||testKitId.equals("RTDS000064V1")
								//2020-09-05 wwjoo BIOplastics 추가	// UKJK 20201005	RVEA_2.0_BIOPLASTICS_ENABLE
								||testKitId.equals("RTDS000072V1") ||testKitId.equals("RTDS000073V1"))
						{
							if(wellInfo.getPanelInfo().getTestKitInfo().getId().equals("RTAP030006V1"))
							{
								if(testKitIdMap.containsKey("RTDS000008V1"))
								{
									testKitId = "RTDS000062V1";
								}
								else if(testKitIdMap.containsKey("RTDS000008V2"))
								{
									testKitId = "RTDS000063V1";
								}
								else if(testKitIdMap.containsKey("RTDS000008V3"))
								{
									testKitId = "RTDS000064V1";
								}
								//2020-09-05 wwjoo BIOplastics 8 strip
								else if(testKitIdMap.containsKey("RTDS000008V4"))	// UKJK 20201005	RVEA_2.0_BIOPLASTICS_ENABLE
								{
									testKitId = "RTDS000072V1";
								}
								//2020-09-05 wwjoo BIOplastics 96 film
								else if(testKitIdMap.containsKey("RTDS000008V5"))	// UKJK 20201005	RVEA_2.0_BIOPLASTICS_ENABLE
								{
									testKitId = "RTDS000073V1";
								}
							}
							else
							{
								if(testKitIdMap.containsKey("RTAP030006V1"))
								{
									testKitIdMap.put("RTAP030006V1", testKitId);
								}
							}
						}
					}
					testKitIdMap.put(wellInfo.getPanelInfo().getTestKitInfo().getId(), testKitId);
				}
			}
			for(SgWellInfo wellInfo : checkWell.values())
			{
				wellInfoMap.remove(wellInfo.getWellId());
			}
			
			if(testKitIdMap.keySet().size() > 1)
			{
				ArrayList<String> fullPanelIds = new ArrayList<String>();
				for(Object key : testKitIdMap.keySet())
				{
					String fullPanelId = testKitIdMap.get(key);
					if(!fullPanelIds.contains(fullPanelId))
					{
						fullPanelIds.add(fullPanelId);
					}
				}
				if(fullPanelIds.size() == 1)
				{
					for(SgWellInfo wellInfo : wellInfoMap.values())
					{
						if(wellInfo.getTestKitInfo() != null && SgTestKitFactory.getTestKitInfo(fullPanelIds.get(0)).getPanelInfo(wellInfo.getPanelInfo().getName()) != null)
						{
							wellInfo.setPanelInfo(SgTestKitFactory.getTestKitInfo(fullPanelIds.get(0)).getPanelInfo(wellInfo.getPanelInfo().getName()));
						}
					}
				}
			}
		}
	}
	
	/**
	 * raw 데이터의 content 를 확인하여 well type 을 반환합니다.
	 * @param value
	 * @return
	 */
	/**
	 * 2019-08-26 wwjoo
	 * 
	 * STD-5,STD-6 Standard 로 추가
	 * 2019-09-05
	 * 품질보증 문제로 일단 456 막음
	 */
	private String getWellControlType(String value)
	{
		if(value.toUpperCase().indexOf("UNKN") != -1)
		{
			return SgControl.SAMPLE;
		}
		if(value.toUpperCase().indexOf("POS") != -1)
		{
			return SgControl.POSITIVE_CONTROL;
		}
		if(value.toUpperCase().indexOf("NEG") != -1)
		{
			return SgControl.NEGATIVE_CONTROL;
		}
		if(value.toUpperCase().indexOf("NTC") != -1)
		{
			return SgControl.NEGATIVE_CONTROL;
		}
		if(value.toUpperCase().indexOf("STD-1") != -1)
		{
			return SgControl.STANDARD;
		}
		if(value.toUpperCase().indexOf("STD-2") != -1)
		{
			return SgControl.STANDARD;
		}
		if(value.toUpperCase().indexOf("STD-3") != -1)
		{
			return SgControl.STANDARD;
		}
		if(value.toUpperCase().indexOf("STD-4") != -1)
		{
			return SgControl.STANDARD;
		}
		if(value.toUpperCase().indexOf("STD-5") != -1)
		{
			return SgControl.STANDARD;
		}
		if(value.toUpperCase().indexOf("STD-6") != -1)
		{
			return SgControl.STANDARD;
		}
		if(value.toUpperCase().indexOf("STD1") != -1)
		{
			return SgControl.STANDARD;
		}
		if(value.toUpperCase().indexOf("STD2") != -1)
		{
			return SgControl.STANDARD;
		}
		if(value.toUpperCase().indexOf("STD3") != -1)
		{
			return SgControl.STANDARD;
		}
		if(value.toUpperCase().indexOf("STANDARD") != -1)
		{
			return SgControl.STANDARD;
		}
		return "";
	}
}

