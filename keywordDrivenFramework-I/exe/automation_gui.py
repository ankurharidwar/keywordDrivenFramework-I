import wx
import wx.lib.agw.aui as aui
import wx.grid
import wx.lib.platebtn as platebtn
import os
import subprocess
import sys
import datetime
import thread
import time
import logging
import optparse


outputLog = 'gui_log.txt'
outputLog_handle = open(outputLog, "w+")
run_all = True

suite_data = [
                    ("Ad-Serving Mobile","adserve.mobileAdServe","MobileAdServingTests", "Ad Serving of All Mobile Ad Formats.", "mobile_adserving"),
                    ("Mobile Https Check","adserve.mobileAdServe","MobileAdServing_HttpsCheck", "Check if all tracker urls starts with https for https requests .", "mobile_adserving"),
                    #("Mobile Language Targeting","adserve.mobileTargeting","TargetingLanguage_TID26", "Ad Serving of Mobile Campaigns With Language Targeting.", "Mobile_langauage_targeting"),
                    #("Mobile OS Version Targeting","adserve.mobileTargeting","TargetingOSVersion_TID3", "Ad Serving Of Mobile Campaigns with OS Version Targeting.", "Mobile_OS_Version_Targeting"),
                    #("Mobile Week Day Targeting","adserve.mobileTargeting","TargetingWeekday_TID2","Ad Serving Of Mobile Campaigns With Weekday Targeting.", "Mobile_Weekday_Targeting"),
                    #("Mobile Adult Content Targeting","adserve.mobileTargeting","TargetingAdultContent_TID23","Ad Serving Of Mobile Campaigns With Adult Content Targeting.", "Mobile_Adult_Content_Targeting"),
                    #("Mobile Device Capabilities Targeting","adserve.mobileTargeting","TargetingDeviceCapabilities_TID10","Ad Serving Of Mobile Campaigns With Device Capabilities Targeting.", "Mobile_Device_Capabilities_Targeting"),
					#("Mobile Publisher Targeting","adserve.mobileTargeting","TargetingPublisher_TID9","Ad Serving Of Mobile Campaigns With Publisher Targeting." ,"Mobile_Publisher_Targeting"),
					#("Mobile Operating System Targeting","adserve.mobileTargeting","TargetingOperatingSystem_TID7","Ad Serving Of Mobile Campaigns With Operating System Targeting.", "Mobile_OS_Targeting"),
                    #("Online Pre-Widget Player","adserve.onlineAdServe","OnlineAdServingTests_PrewidgetPlayer","Ad Serving Of Online Ad Formats On PrewidgetPlayer.", "Online_Pre-Widget_Player"),
                    #("Online SWC Player","adserve.onlineAdServe","OnlineAdServingTests_SWCPlayer","Ad Serving Of Online Ad Formats On SWCPlayer.", "Online_SWC_Player"),
                    #("Online V4 Player","adserve.onlineAdServe","OnlineAdServingTests_V4Player","Ad Serving Of Online Ad Formats On V4Player.", "Online_V4_Player"),
                    #("Online Vast2VDO Player","adserve.onlineAdServe","OnlineAdServingTests_Vast2Vdo","Ad Serving Of Online Ad Format: Vast2Vdo.", "Online_Vast2VDO_Player"),
                    #("Sanity Suite - Channel & Campaign Creation","sanityTestSuite.mobile","SanityTestSuite_ChannelCampaignCreation","Sanity Test Suite For Channel & Campaign Creation.", "Sanity_Suite-Channel_Campaign_Creation"),
                    ("Transformer Portal Checks","Portal Checks","Portal UI CRUD Operations","Test Suite For Transformer Portal Checks.", "Sanity_Suite-Portal_Checks"),
					("SSP Portal Checks","portal","LaunchSSPTests","Test Suite For SSP Portal Checks.", "Sanity_Suite-Portal_Checks"),
                    ("E2E Verification","Portal Checks","Portal UI CRUD Operations","Test Suite For E2E Checks.", "Sanity_Suite-E2E_Checks"),
					("E2E Checks","e2etest","LaunchE2ETest","Test Suite For E2E Checks.", "Sanity_Suite-E2E_Checks"),
                    #("Mopub RTB Version 1.0","rtb","MopubTests_V1_0","Test Suite For Mopub RTB v1.0."),
                    #("Mopub RTB Version 2.1","rtb","MopubTests_V2_1","Test Suite For Mopub RTB v2.1."),
                    #("Mopub RTB","rtb","MopubTests_204_response","Test Suite For Mopub RTB." ,"Mopub_RTB"),
                    #("Nexage RTB","rtb","NexageTests","Test Suite For Nexage RTB.", "Nexage_RTB"),
                    #("Nexage RTB","rtb","NexageTestsFast","Test Suite For Nexage RTB.", "Nexage_RTB_FAST"),
                    #("Smaato RTB","rtb","SmaatoTests","Test Suite For Smaato RTB.", "Smaato_RTB"),
                    ("SDK Ad Serving","sdk","SDKAdServingTest","Mobile Ad Serving Using SDK.", "SDK"),
                    #("Light Weight SDK","sdk","LWSDKAdServingTest","Mobile Ad Serving Using LW SDK.", "Light_Weight_SDK"),
                    #("Mobile Portal Smoke Tests","Smoke Test","Smoke Test","Smoke Test Suite For Mobile Portal.", "Mobile_Portal_Smoke_Tests"),
                    ("Chocolate Request Verification","chocolate","LaunchChocolateTests","Chocolate Request/Response Validation.", "chocolate_Get_Request_Verification")

                    ]




class MDIFrame(wx.MDIParentFrame):
    def __init__(self):


        wx.MDIParentFrame.__init__(self, None, -1, "VDOPIA Automation Test Deck ", size =(1150, 720))
        menu = wx.Menu()
        menu.Append(5000, "&Configuration")
        menu.Append(5001, "&Exit")
        menubar = wx.MenuBar()
        menubar.Append(menu, "&Options")
        self.SetMenuBar(menubar)
        self.Bind(wx.EVT_MENU, self.OnNewWindow, id=5000)
        self.Bind(wx.EVT_MENU, self.OnExit, id=5001)
        self.mgr = aui.AuiManager(self, aui.AUI_MGR_DEFAULT
                                       |aui.AUI_MGR_TRANSPARENT_DRAG
                                       |aui.AUI_MGR_ALLOW_ACTIVE_PANE                                     
                                       |aui.AUI_MGR_TRANSPARENT_HINT)
        bsizer = wx.BoxSizer()
        self.inputPanel = wx.Panel(self, -1)
        self.logPanel = wx.Panel(self, -1)
        self.logPanel.SetSizerAndFit(bsizer)
        color = (204,204,153)

        
        self.png = wx.StaticBitmap(self, -1, wx.Bitmap( os.path.dirname(os.path.realpath(__file__)) + "/image.png", wx.BITMAP_TYPE_ANY))
        #self.SetBackgroundColour((179, 179, 179))
        self.SetBackgroundColour(color)
        #Heading = wx.StaticText(self, -1, label="Automation Test Deck", pos = (450, 10), style = wx.ALIGN_CENTER)
        #font = wx.Font(22, wx.DECORATIVE, wx.NORMAL, wx.NORMAL, underline=False)
        
        #Heading.SetFont(font)        
        
        # Creating a separate Panel to show the logfile content. The Filename needs to be passed HERE so that the content is dislayed.
        self.logPanel.logctrl = wx.TextCtrl(self.logPanel, value=" Status Updates are available here", pos=(20, 20), size=(580,280),style=wx.TE_MULTILINE|wx.SUNKEN_BORDER|wx.TE_DONTWRAP|wx.EXPAND)
        self.logPanel.logctrl.SetBackgroundColour(color)
        bsizer.Add(self.logPanel.logctrl,1,wx.EXPAND)



        self.testsuite_data = [
                    ("Ad-Serving Mobile","adserve.mobileAdServe","MobileAdServingTests", "Ad Serving of All Mobile Ad Formats."),
                    ("Mobile Https Check","adserve.mobileAdServe","MobileAdServing_HttpsCheck", "Check if all tracker urls starts with https for https requests .", "mobile_adserving"),
                    ("Mobile Language Targeting","adserve.mobileTargeting","TargetingLanguage_TID26", "Ad Serving of Mobile Campaigns With Language Targeting."),
                    ("Mobile OS Version Targeting","adserve.mobileTargeting","TargetingOSVersion_TID3", "Ad Serving Of Mobile Campaigns with OS Version Targeting."),
                    ("Mobile Week Day Targeting","adserve.mobileTargeting","TargetingWeekday_TID2","Ad Serving Of Mobile Campaigns With Weekday Targeting."),
                    ("Mobile Adult Content Targeting","adserve.mobileTargeting","TargetingAdultContent_TID23","Ad Serving Of Mobile Campaigns With Adult Content Targeting."),
                    ("Mobile Device Capabilities Targeting","adserve.mobileTargeting","TargetingDeviceCapabilities_TID10","Ad Serving Of Mobile Campaigns With Device Capabilities Targeting."),
					("Mobile Publisher Targeting","adserve.mobileTargeting","TargetingPublisher_TID9","Ad Serving Of Mobile Campaigns With Publisher Targeting."),
					("Mobile Operating System Targeting","adserve.mobileTargeting","TargetingOperatingSystem_TID7","Ad Serving Of Mobile Campaigns With Operating System Targeting."),
                    ("Online Pre-Widget Player","adserve.onlineAdServe","OnlineAdServingTests_PrewidgetPlayer","Ad Serving Of Online Ad Formats On PrewidgetPlayer."),
                    ("Online SWC Player","adserve.onlineAdServe","OnlineAdServingTests_SWCPlayer","Ad Serving Of Online Ad Formats On SWCPlayer."),
                    ("Online V4 Player","adserve.onlineAdServe","OnlineAdServingTests_V4Player","Ad Serving Of Online Ad Formats On V4Player."),
                    ("Online Vast2VDO Player","adserve.onlineAdServe","OnlineAdServingTests_Vast2Vdo","Ad Serving Of Online Ad Format: Vast2Vdo."),
                    ("Sanity Suite - Channel & Campaign Creation","sanityTestSuite.mobile","SanityTestSuite_ChannelCampaignCreation","Sanity Test Suite For Channel & Campaign Creation."),
                    ("Sanity Suite - Portal Checks","Portal Checks","Portal UI CRUD Operations","Test Suite For Mobile Portal Checks."),
                    #("Mopub RTB Version 1.0","rtb","MopubTests_V1_0","Test Suite For Mopub RTB v1.0."),
                    #("Mopub RTB Version 2.1","rtb","MopubTests_V2_1","Test Suite For Mopub RTB v2.1."),
                    ("Mopub RTB","rtb","MopubTests_204_response","Test Suite For Mopub RTB."),
                    ("Nexage RTB","rtb","NexageTests","Test Suite For Nexage RTB."),
                    ("Smaato RTB","rtb","SmaatoTests","Test Suite For Smaato RTB."),
                    ("SDK Ad Serving","sdk","SDKAdServingTest","Mobile Ad Serving Using SDK.", "SDK"),
                    #("Light Weight SDK","sdk","LWSDKAdServingTest","Mobile Ad Serving Using LW SDK."),
                    ("Mobile Portal Smoke Tests","Smoke Test","Smoke Test","Smoke Test Suite For Mobile Portal."),
                    ("chocolate Get Request Verification","chocolate","chocolateTests","chocolate Inbound Request Validation.")

                    ]



        self.testsuite_data = suite_data

		# Create a wxGrid object
        grid = wx.grid.Grid(self.GetClientWindow(), -1, pos = (50,50), size = (1100,440))
        

        self.all = wx.CheckBox(self.GetClientWindow(), -1, '' ,pos=(30, 60))
        self.all.SetValue(True)



        # Then we call CreateGrid to set the dimensions of the grid
        # (DEfault 4 columns and Rows are Dynamically populated)
        grid.CreateGrid(len(self.testsuite_data), 4)

        grid.SetColLabelValue(0, "ID")
        grid.SetColSize(0,30)
        #grid.SetColLabelAlignment(0, center)
        grid.SetColLabelValue(1, "Component Name")
        grid.SetColSize(1,220)
        grid.SetColLabelValue(2, "Test Suite Name")
        grid.SetColSize(2,320)
        grid.SetColLabelValue(3, "Description")
        grid.SetColSize(3,900)
        grid.SetColLabelSize(35)
        grid.SetRowLabelSize(0)
        grid.SetLabelBackgroundColour(color)


        YSTART = 85
        YGAP = 19
        self.checkbox = []
        testsuite_data_row_count = 0
        for entry in self.testsuite_data:
            #print "This is row : " + str(testsuite_data_row_count)
            grid.SetCellValue(testsuite_data_row_count, 0, str(testsuite_data_row_count + 1))
            grid.SetCellAlignment(testsuite_data_row_count, 0, wx.ALIGN_CENTRE, wx.ALIGN_CENTRE)
            grid.SetReadOnly(testsuite_data_row_count, 0)
            grid.SetCellValue(testsuite_data_row_count, 1, entry[0])
            grid.SetReadOnly(testsuite_data_row_count, 1)
            grid.SetCellValue(testsuite_data_row_count, 2, entry[2])
            grid.SetReadOnly(testsuite_data_row_count, 2)
            grid.SetCellValue(testsuite_data_row_count, 3, entry[3])
            grid.SetReadOnly(testsuite_data_row_count, 3)
            self.checkbox.append(wx.CheckBox(self.GetClientWindow(), -1, '' ,pos=(30, (YSTART + testsuite_data_row_count * YGAP))))
            if entry[2] == "Smoke Test" or entry[2] == "MopubTests_V1_0":
                self.checkbox[testsuite_data_row_count].SetValue(False)
            else:
                self.checkbox[testsuite_data_row_count].SetValue(True)


            if run_all == False:

                if entry[4] in suite:
                    self.checkbox[testsuite_data_row_count].SetValue(True)
                else:
                    self.checkbox[testsuite_data_row_count].SetValue(False)



            testsuite_data_row_count = testsuite_data_row_count + 1

        self.checkbox_serve_new_campaigns = wx.CheckBox(self.inputPanel, -1, 'Serve New Sanity Campaigns' ,pos=(25, 50))
        self.checkbox_serve_new_campaigns.Disable();
        self.checkbox_serve_new_campaigns.Hide();
        
        
        self.checkbox_Rerun_failtestcases = wx.CheckBox(self.inputPanel, -1, 'ReRun Failed TestCases' ,pos=(25, 70))
        self.checkbox_Rerun_failtestcases.Hide();

        self.checkbox_Use_ExistingchocolateURL = wx.CheckBox(self.inputPanel, -1, 'Use Existing chocolate URLs' ,pos=(25, 90))
        self.checkbox_Use_ExistingchocolateURL.Disable();
        
        self.StartTest_Button = wx.Button(self.inputPanel, label = "Execute Selected Testsuites", pos=(20, 100) , size=(250, 50))


        Browserdata = ['Chrome', 'FireFox']

        self.Browser = Browserdata[0]
        st = wx.StaticText(self.inputPanel, label='Select Browser to execute tests : ', pos=(25, 20))
        BrowserList= wx.ComboBox(self.inputPanel , pos=(250, 20), choices=Browserdata, style=wx.CB_READONLY)

        BrowserList.Bind(wx.EVT_COMBOBOX, self.OnBrowserListSelect)



        self.Bind(wx.EVT_CHECKBOX, self.EvtUseExistingUrl, self.checkbox_Use_ExistingchocolateURL)
        
        self.Bind(wx.EVT_CHECKBOX, self.EvtSelectAll, self.all)

        self.Bind(wx.EVT_BUTTON, self.ExecuteSuites, self.StartTest_Button)


        self.BuildPain()

    def OnBrowserListSelect(self, e):

        self.Browser = e.GetString()



    def EvtUseExistingUrl(self, event):

        if self.checkbox_Use_ExistingchocolateURL.IsChecked():
            Message = wx.MessageDialog(self.inputPanel, "Please use file ~Vdopia_Automation/tc_data/chocolate/PasteExistingChocolateRequests.xls", "chocolate Existing URL Use Alert", wx.OK)
            Message.ShowModal()
            Message.Destroy()
            pass
        else:
            pass


    def EvtSelectAll(self, event):

        if self.all.IsChecked():
            for box in range(len(self.checkbox)):
                #self.checkbox[box].SetValue(True)
                if self.testsuite_data[box][2] == "Smoke Test" or self.testsuite_data[box][2] == "MopubTests_V1_0":
                    pass
                else:
                    self.checkbox[box].SetValue(True)

        else:
            for box in range(len(self.checkbox)):
                if self.testsuite_data[box][2] == "Smoke Test" or self.testsuite_data[box][2] == "MopubTests_V1_0":
                    pass
                else:
                    self.checkbox[box].SetValue(False)


    def BuildPain(self):                             
        self.mgr.AddPane(self.GetClientWindow(),
                         aui.AuiPaneInfo().Name("book").Caption("Notebook").
                         CenterPane().CaptionVisible(True).Dockable(True).Floatable(False).
                         BestSize((350,350)).CloseButton(False).MaximizeButton(True)
                         )    
        self.mgr.AddPane(self.inputPanel,
                        aui.AuiPaneInfo().Name("input").Caption("").
                         CaptionVisible(True).Bottom().Dockable(True).Floatable(False).
                         BestSize((150,150)).CloseButton(False).MaximizeButton(False)
                         )
        self.mgr.AddPane(self.logPanel,
				        aui.AuiPaneInfo().Name("log").Caption("Status Update").
				         CaptionVisible(True).Bottom().Dockable(True).Floatable(False).
				         BestSize((150,150)).CloseButton(False).MaximizeButton(True)
				         )
        
        self.mgr.Update()



    def OnExit(self, evt):
        self.Close(True)

		
    
    def OnNewWindow(self, evt):
            #win = wx.MDIChildFrame(self, -1, "Configuration Changes")
            #win.Show(True)
            #childPanel = win.Panel(self, -1)
        self.child = ChildFrame(self)
        self.child.Show()



    def ExecuteSuites(self, event):

        self.startRunning()

    def CreateTestNG(self, TestList):

        file1 = os.path.realpath(__file__)
        dir1 = os.path.dirname(os.path.realpath(__file__))
        if os.name == "nt":
            self.testNG_File = dir1 + "\\testng_gui.xml"
        else:
            self.testNG_File = dir1 + "/testng_gui.xml"


        print("\n Dynamic TestNG file is created at location : " + self.testNG_File)
        self.logPanel.logctrl.AppendText("\n Dynamic TestNG file is created at location : " + self.testNG_File + "\n")

        testNG_handle = open(self.testNG_File, "w+")
        print("length of Testlist is " + str(len(TestList)))
        for Data_testNG in range(len(TestList)):
            #print("Data Added is : " + str(TestList[Data_testNG]))
            testNG_handle.write(str(TestList[Data_testNG]))

        testNG_handle.close()


    def startRunning(self):
        selected = 'No'
        Smoke_select = "No"
        Run_test = "Yes"
        for row in range(len(self.checkbox)):
            if self.checkbox[row].IsChecked():
                selected = 'Yes'


        if selected == 'Yes':

            for suite_row in range(len(self.checkbox)):
                    if self.checkbox[row].IsChecked():
                        if self.testsuite_data[row][1] == "Smoke Test":
                            Smoke_select = "Yes"



            self.logPanel.logctrl.SetValue (" Execution Started ....")
            self.logPanel.logctrl.AppendText("Execution Started" + "\n")
            #self.StartTest_Button.Disable()
            file1 = os.path.realpath(__file__)
            dir1 = os.path.dirname(os.path.realpath(__file__))

            self.SetAutomationEnv()

            NOW = datetime.datetime.now()
            self.logFileLocation = self.AUTOMATION_HOME + '/logs/' + NOW.strftime("%m_%d_%Y")
            self.logFileName= NOW.strftime("%m_%d_%Y_%H%M%S")+".txt"

            try:


                if self.checkbox_serve_new_campaigns.GetValue():
                    serveSanityCampaign = 'Yes'
                else:
                    serveSanityCampaign = 'No'
                    
                if self.checkbox_Rerun_failtestcases.GetValue():
                    ReRun = 'Yes'
                else:
                    ReRun = 'No'                    
                
                if              self.checkbox_Use_ExistingchocolateURL.GetValue():
                    self.useExistingURL = 'Yes'
                    
                    print("Existing URL will be used for chocolate Request Validation." );
        
                else:
                    self.useExistingURL = 'Yes'

                print("Selected Browser is : " + self.Browser);
                

                XML_START_CONTENT = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Vdopia_Automation" parallel="false">
    <listeners>
        <listener class-name="org.uncommons.reportng.HTMLReporter"/>
        <listener class-name="org.uncommons.reportng.JUnitXMLReporter"/>
    </listeners>
                                    """
                XML_LOG_DETAIL = """
    <parameter name="browser"  value="%(Browser)s"/>
    <parameter name="logFileLocation"  value="%(logFileLocation)s"/>
    <parameter name="logFileName"  value="%(logFileName)s"/>
    <parameter name="serveSanityCampaign" value="%(serveSanityCampaign)s"/>
    <parameter name="useExistingURL" value="%(useExistingURL)s"/>
    <parameter name="ReRun" value="%(ReRun)s"/>
    <parameter name="smokeTest" value="No" />
    <test name="AdServe">
        <classes>
            <class name="projects.TestSuiteClass"/>
                                    """%({'Browser' : self.Browser, 'logFileLocation' : self.logFileLocation, 'logFileName' : self.logFileName, 'serveSanityCampaign' : serveSanityCampaign, 'ReRun' : ReRun, 'useExistingURL' : self.useExistingURL})
                XML_END_CONTENT =   """
        </classes>
    </test>
</suite>
                                    """


                XML_SMOKE_CONTENT = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Vdopia_Automation" parallel="false">
	<listeners>
		<listener class-name="org.uncommons.reportng.HTMLReporter" />
		<listener class-name="org.uncommons.reportng.JUnitXMLReporter" />
	</listeners>
	<parameter name="browser"  value="%(Browser)s"/>
	<parameter name="logFileLocation"  value="%(logFileLocation)s"/>
    <parameter name="logFileName"  value="%(logFileName)s"/>
    <parameter name="serveSanityCampaign" value="%(serveSanityCampaign)s"/>
    <parameter name="ReRun" value="%(ReRun)s"/>
    <parameter name="smokeTest" value="Yes" />
	<test name="SmokeTest">
		<classes>
			<class name="projects.TestSuiteClass" />
			<class name="projects.portal.LaunchTransformerPortalTests" />
                                    """%({'Browser' : self.Browser, 'logFileLocation' : self.logFileLocation, 'logFileName' : self.logFileName, 'serveSanityCampaign' : serveSanityCampaign, 'ReRun' : ReRun})

                testNG_data =[]
                testNG_data.append(XML_START_CONTENT)
                testNG_data.append(XML_LOG_DETAIL)

                for row in range(len(self.checkbox)):

                    if self.checkbox[row].IsChecked():

                        if self.testsuite_data[row][1] != "Smoke Test" and Smoke_select == "Yes":
                            SmokeAlert = wx.MessageDialog(self.inputPanel, "Smoke Test Suite can not be executed with other test Suites. Please un-check other test Suites OR Smoke Test Suite.", "Execution Alert", wx.OK)
                            SmokeAlert.ShowModal()
                            SmokeAlert.Destroy()
                            Run_test = "No"
                            break
                        else:
                            if self.testsuite_data[row][1] == "Smoke Test":
                                testNG_data = [XML_SMOKE_CONTENT]
                                break

                            if self.testsuite_data[row][1] == "Portal Checks":
                                testNG_data.append("""
           <class name="projects.portal.LaunchTransformerPortalTests" />
                                           """)

                            elif self.testsuite_data[row][2] == "SanityTestSuite_ChannelCampaignCreation" :
                                if self.checkbox_serve_new_campaigns.IsChecked():
                                    testNG_data.append("""
            <class
				name="projects.sanityTestSuite.mobile.SanityTestSuite_ChannelCampaignCreation">
				<methods>
					<include name="SanityTest_ChannelCampaignCreation" />
					<include name="SanityTest_ServeCampaigns" />
				</methods>
			</class>
                                    """)

                                else:
                                    testNG_data.append("""
			<class
				name="projects.sanityTestSuite.mobile.SanityTestSuite_ChannelCampaignCreation">
				<methods>
					<include name="SanityTest_ChannelCampaignCreation" />
					<exclude name="SanityTest_ServeCampaigns" />
				</methods>
			</class>                                    """)


                            else:
                                testNG_data.append("""
            <class name="projects."""+self.testsuite_data[row][1]+"." + self.testsuite_data[row][2]+""""/>
                                """)



                testNG_data.append(XML_END_CONTENT)

                """
                for i in range(len(testNG_data)):
                    print ("Element " + str(i) + " is : " + str(testNG_data[i]))

                """
                self.CreateTestNG(testNG_data)

                ########################  RUNNING SELECTED TESTSUITS  ######################

                #f1 = open("/Users/vdopia/Downloads/Vdopia_Automation_V_1.3/Vdopia_automation/exe/frames.py","r")
                #self.logPanel.logctrl.SetValue("Logfile is created at location :  " + self.logFileLocation + "/" + self.logFileName )
                self.logPanel.logctrl.AppendText("Logfile is created at location :  " + self.logFileLocation + "/" + self.logFileName +"\n")
                #f1.close()
                wx.YieldIfNeeded()

                if Run_test == "Yes":
                    self.StartTest_Button.Disable()
                    self.RunTest()


            except Exception, e :
                print("\n Exception while executing test suites : ")
                print("\n ExecuteSuites() Exception : " + e.message)
                self.exceptionTestEnd()
        else:

            Message = wx.MessageDialog(self.inputPanel, "Please select atleast 1 test suite for execution", "Execution Alert", wx.OK)
            Message.ShowModal()
            Message.Destroy()
            pass





    def appendEnvVar(self, var , val):
        try:

            setVal = None
            currentVal = os.getenv(var)

            if currentVal is None:
                setVal = val
            else:
                if os.name == "nt":
                    setVal = currentVal + ';' + val
                else:
                    setVal = currentVal + ':' + val

            os.environ[var] = setVal
        except Exception, e :
            print("\n Exception during updating environment variable")
            print("\n appendEnvVar() Exception : " + e.message)
            self.exceptionTestEnd()

    def SetAutomationEnv(self):
        try:
            currentFileLoc = os.path.dirname(os.path.realpath(__file__))
            self.AUTOMATION_HOME = os.path.dirname(currentFileLoc)
            ###  IF automation Home is SET. Below code will make AUTOMATION _HOME = NONE

            #A_HOME = os.getenv('AUTOMATION_HOME')
            #if A_HOME is not None:
            #    print("Environment Variable 'AUTOMATION_HOME' is Already SET. Resetting AUTOMATION_HOME VARIABLE")
            #    os.environ['AUTOMATION_HOME'] = None

            #### SET THE AUTOMATION_HOME TO  NEW AUTOMATION_HOME########

            #self.appendEnvVar('AUTOMATION_HOME', self.AUTOMATION_HOME)

            os.environ['AUTOMATION_HOME'] = self.AUTOMATION_HOME
            print ("Automation home is " + self.AUTOMATION_HOME)
            self.logPanel.logctrl.AppendText("Automation home is " + self.AUTOMATION_HOME + "\n")
            outputLog_handle.write("Automation home is " + self.AUTOMATION_HOME)

            if not os.path.exists(self.AUTOMATION_HOME + "/bin"):
                os.makedirs(self.AUTOMATION_HOME + "/bin")


            if os.name == "nt":
                if os.getenv('JAVA_HOME') is None:
                    execute = subprocess.Popen(["""where""","""javac"""], stdout=subprocess.PIPE, stdin=subprocess.PIPE, stderr=subprocess.STDOUT, shell=True)
                    (execute_result, execute_error) = execute.communicate()
                    if ("INFO: Could not find" in execute_result ) and execute_error is None :
                        print("Automation is not able to set JAVA_HOME variable. Please set manually")
                        outputLog_handle.write("Automation is not able to set JAVA_HOME variable. Please set manually")
                        self.exceptionTestEnd()

                    else:
                        os.environ['JAVA_HOME'] = os.path.dirname(os.path.dirname(execute_result))

                self.ANT_HOME = self.AUTOMATION_HOME + "\\tpt\\apache-ant-1.9.3_windows"

                os.environ['ANT_HOME'] = self.ANT_HOME
                self.appendEnvVar('PATH', self.ANT_HOME + '\\bin')
                print("\n JAVA_HOME PATH IS : '" + os.getenv('JAVA_HOME') + "'")
                outputLog_handle.write("JAVA_HOME PATH IS : " + os.getenv('JAVA_HOME'))
                self.logPanel.logctrl.AppendText("JAVA_HOME PATH IS : " + os.getenv('JAVA_HOME') + "\n")


            else:
                if os.getenv('JAVA_HOME') is None:
                    execute = subprocess.Popen(["""which javac"""], stdout=subprocess.PIPE, stdin=subprocess.PIPE, stderr=subprocess.STDOUT, shell=True)
                    (execute_result, execute_error) = execute.communicate()
                    if (("INFO: Could not find" in execute_result ) or (execute_result is None)) and execute_error is None :
                        print("Automation is not able to set JAVA_HOME variable. Please set manually")
                        outputLog_handle.write("Automation is not able to set JAVA_HOME variable. Please set manually")
                        self.exceptionTestEnd()

                    else:
                        os.environ['JAVA_HOME'] = os.path.dirname(os.path.dirname(execute_result))
                        print("\n JAVA_HOME PATH IS : '" + os.getenv('JAVA_HOME') + "'")
                        outputLog_handle.write("JAVA_HOME PATH IS : " + os.getenv('JAVA_HOME'))
                        self.logPanel.logctrl.AppendText("JAVA_HOME PATH IS : " + os.getenv('JAVA_HOME') + "\n")

                self.ANT_HOME = self.AUTOMATION_HOME + "/tpt/apache-ant-1.9.3_unix"
                os.environ['ANT_HOME'] = self.ANT_HOME
                self.appendEnvVar('PATH', self.ANT_HOME + '/bin')

            #   Set PAth for Sikuli Lib in windows

            if os.name == "nt":
                self.appendEnvVar('PATH', self.AUTOMATION_HOME + '\\ext_jars\\libs')


            conf_handle = open(self.AUTOMATION_HOME + """/conf/qaconf.properties""", "r")

            for detail in conf_handle:
                detail = detail.strip()
                if not detail.startswith("#"):

                    if os.name == "nt":
                        if detail.startswith("AndroidSDK_Windows"):
                            ANDROID_HOME = detail.split("=")[1].strip()
                            os.environ['ANDROID_HOME'] = ANDROID_HOME
                    else:
                        if detail.startswith("AndroidSDK_Mac"):
                            ANDROID_HOME = detail.split("=")[1].strip()
                            os.environ['ANDROID_HOME'] = ANDROID_HOME

            print("\n FINAL AUTOMATION_HOME PATH IS : '" + os.getenv('AUTOMATION_HOME') + "'")
            outputLog_handle.write("FINAL AUTOMATION_HOME PATH IS : '" + os.getenv('AUTOMATION_HOME') + "'")
            self.logPanel.logctrl.AppendText("FINAL AUTOMATION_HOME PATH IS : " + os.getenv('AUTOMATION_HOME') + "\n")

            print("\n ANDROID_HOME PATH IS : '" + os.getenv('ANDROID_HOME') + "'")
            outputLog_handle.write("ANDROID_HOME PATH IS : " + os.getenv('ANDROID_HOME'))
            self.logPanel.logctrl.AppendText("ANDROID_HOME PATH IS : " + os.getenv('ANDROID_HOME') + "\n")


            wx.YieldIfNeeded()

        except Exception, e:
            #print("SetAutomationEnv() exception while running test is " + e.message)
            outputLog_handle.write("SetAutomationEnv() exception while running test is " + e.message)
            self.logPanel.logctrl.AppendText("SetAutomationEnv() exception while running test is " + e.message + "\n")
            self.exceptionTestEnd()

    def RunTest(self):
        try:
            print("###################################################################")
            print("                    COMPILING & EXECUTING  JAVA FILES                           ")
            print("###################################################################")
            self.logPanel.logctrl.AppendText("Compiling and executing Java Files" + "\n")

            outputLog_handle.write("###################################################################")
            outputLog_handle.write("                    COMPILING  & EXECUTING JAVA FILES                           ")
            outputLog_handle.write("###################################################################")



            try:
                if os.name == "nt":

                    print ("Running Execute_Ant.bat for windows system")
                    execute_ant = os.path.dirname(os.path.realpath(__file__)) + "/Execute_Ant.bat"
                    execute = subprocess.Popen([execute_ant], stdout=subprocess.PIPE, stdin=subprocess.PIPE, stderr=subprocess.STDOUT, shell=True)
                else:
                    _cmd = """cd .. ; ant -v """
                    print ("Running command : %s" %(_cmd))
                    self.logPanel.logctrl.AppendText("Running command : %s" %(_cmd) + "\n")
                    outputLog_handle.write("Running command : %s" %(_cmd))
                    execute = subprocess.Popen(["""%(_cmd)s"""%({'_cmd' : _cmd})], stdout=subprocess.PIPE, stdin=subprocess.PIPE, stderr=subprocess.STDOUT, shell=True)
                    
                (execute_result, execute_error) = execute.communicate()
                print ("STDOUT : %s" %(execute_result))
                self.logPanel.logctrl.AppendText("STDOUT : %s" %(execute_result) + "\n")
                outputLog_handle.write("STDOUT : %s" %(execute_result))
                if execute_error is not None:
                    print ("Error in execution : %s" %(execute_error))
                    self.logPanel.logctrl.AppendText("Error in execution : %s" %(execute_error) + "\n")
                    outputLog_handle.write("Error in execution : %s" %(execute_error))

            except Exception, e:
                print ("Exception while executing command:  %s "+e.message)
                self.logPanel.logctrl.AppendText("Exception while executing command:  %s "+e.message +"\n")
                wx.YieldIfNeeded()
                outputLog_handle.write("Exception while executing command:  %s "+e.message)
                self.exceptionTestEnd()






            print("###################################################################")
            print("                 TestSuite Execution is Complete                    ")
            print("###################################################################")
            self.logPanel.logctrl.AppendText("################ TestSuite Execution is Complete #################"+"\n")
            wx.YieldIfNeeded()
            outputLog_handle.write("###################################################################")
            outputLog_handle.write("                 TestSuite Execution is Complete                    ")
            outputLog_handle.write("###################################################################")
            self.StartTest_Button.Enable()
            #f4 = open(self.logFileLocation + "/" + self.logFileName,"r")
            #print(f4.read())
            #self.logPanel.logctrl.SetValue(f4.read())
            #f4.close()

        except Exception, e:
            print("\n RunTest() exception while running test is " + e.message)
            self.logPanel.logctrl.AppendText("RunTest() exception while running test is " + e.message + "\n" )
            outputLog_handle.write("RunTest() exception while running test is " + e.message)
            f5 = open(self.logFileLocation + "/" + self.logFileName,"r")
            #print(f5.read())
            print("test end here ")
            self.logPanel.logctrl.AppendText("test end here " + "\n")
            wx.YieldIfNeeded()
            f5.close()
            self.exceptionTestEnd()

    def exceptionTestEnd(self):
        print("\n test end here ")
        print("###################################################################")
        print("      TestSuit Execution is STOPPED because of exception           ")
        print("###################################################################")
        self.logPanel.logctrl.AppendText("      TestSuit Execution is STOPPED because of exception           " + "\n")
        wx.YieldIfNeeded()
        outputLog_handle.write("###################################################################")
        outputLog_handle.write("      TestSuit Execution is STOPPED because of exception           ")
        outputLog_handle.write("###################################################################")
        self.StartTest_Button.Enable()





class ChildFrame(wx.Frame):
    def __init__(self, parent):
        color = (204,204,153)
        wx.Frame.__init__(self, None, size=(650,500), title='Configuration Changes')
        self.parent = parent
        csizer = wx.BoxSizer(wx.VERTICAL)

        pan = wx.Panel(self, pos = (25, 100))
        #pan.SetSizerAndFit(csizer)
        pan.SetSizer(csizer)

        pan.SetBackgroundColour(color)

        #buttpan = wx.Panel(pan,-1, size = (600,40), pos = (25,450))
        #buttpan.SetBackgroundColour('BLACK')
        self.but_save = wx.Button(pan,-1, pos=(50,350), label='Save')
        self.but_cancel = wx.Button(pan,-1, pos=(250,350), label='Cancel')
        btnSizer = wx.BoxSizer(wx.HORIZONTAL)
        btnSizer.Add(self.but_save, 0, wx.ALL, 25, )
        btnSizer.Add(self.but_cancel, 0, wx.ALL, 25)
        #btnSizer.Add(self.but_save)
        #btnSizer.Add(self.but_cancel)
        
		#buttpan.SetSizer(csizer)
		#csizer.Add(pan,6,wx.EXPAND)
        #csizer.Add(buttpan,1,wx.EXPAND)
        #buttpan.SetSizerAndFit(csizer)
        #bpan.SetBackgroundColour(color)
        self.txt = wx.TextCtrl(pan, -1, pos=(25,100), size=(600,300), style=wx.TE_MULTILINE|wx.SUNKEN_BORDER|wx.TE_DONTWRAP)
        self.txt.SetBackgroundColour(color)
        csizer.Add(self.txt ,8,wx.EXPAND)
        csizer.Add( btnSizer ,1,wx.ALL|wx.CENTER)
        #self.SetSizer(csizer)
        #csizer.Add(self.txt, 1, wx.EXPAND)
        currentFileLoc = os.path.dirname(os.path.realpath(__file__))
        A_HOME = os.path.dirname(currentFileLoc)
        self.Conf_File = A_HOME+ "/conf/qaconf.properties"
        Config = file( self.Conf_File,"r")
        #Config = file("/Users/vdopia/Downloads/Vdopia_Automation_V_1.3/Vdopia_automation/tc_data/adserve/mobileAdServe/DataToFormURL/TestDataToFormURL.xls","r")
        self.txt.SetValue(Config.read())
        #self.but_save = wx.Button(pan,-1, pos=(180,400), label='Save')
        #self.but_cancel = wx.Button(pan,-1, pos=(300,400), label='Cancel')
        self.Bind(wx.EVT_BUTTON, self.onSavebutton, self.but_save)
        self.Bind(wx.EVT_BUTTON, self.onCanbutton, self.but_cancel)

    def onSavebutton(self, evt):
        itcontains = self.txt.GetValue()
        filehandle=open(self.Conf_File,'w')
        filehandle.write(itcontains)
        filehandle.close()
        self.Close(True)


    def onCanbutton(self, evt):
	    self.Close(True)


if __name__ == "__main__":

    #  Setting help options of utility
    #usage_msg = sys.argv[0]+" -e silent -L log_file_location"
    usage_msg = "\n To Run all the test suites : \n" + sys.argv[0]+""" -e  """ + "\n\n To Run specific test suites  : \n" + sys.argv[0]+""" -t  mobile_adserving,chocolate_Get_Request_Verification,Mopub_RTB""" + "\n\n To list all the test suites available for execution : \n" + sys.argv[0]+""" -l """


    parser = optparse.OptionParser(usage_msg)

    parser.add_option("-a", "--execute_all",dest="execute_all",help="Start Executing all the available testsuites.", action="store_true", default=False)
    #parser.add_option("-L", "--logFile",dest="logFile",help="specifies the log file name. Default file location is : Vdopia_Automation/log/<CURRENT DATE>/<systemtime>", default = None, type="string", action="store")
    parser.add_option("-l", "--list_testsuites",dest="list_testsuites",help="List the name of testsuites available for the execution.", action="store_true", default=False)
    parser.add_option("-t", "--execute_testsuites",dest="execute_testsuites",help="Start executing given testsuite list", default ="None")
    parser.add_option("-g", "--gui",dest="gui",help="Start executing given testsuite list", default ="yes")

    (options,args)=parser.parse_args()
    execution_all = options.execute_all
    list_testsuites = options.list_testsuites
    gui = options.gui
    execute_testsuites = options.execute_testsuites

    if list_testsuites == True:
        print "############      List of the test suites available for execution ###########  "
        print ""

        printsuite = ""
        for i in range(len(suite_data)):
            printsuite = printsuite + suite_data[i][4]+","
            #print "       " + suite_data[i][4]

        print printsuite
        print "###############################################################################"
        exit()


    if execution_all == True:
        run_all = True
        app = wx.App(0)
        frame = MDIFrame()
        frame.startRunning()
        exit()


    if execution_all == False and list_testsuites == False and execute_testsuites == "None" and gui == "yes":
        app = wx.App(0)
        frame = MDIFrame()
        frame.CenterOnScreen()
        frame.Show()
        app.MainLoop()

    if execute_testsuites != "None":
        print " execute_testSuite = " + execute_testsuites
        print " execute All Value = " + str(execution_all)
        suite = execute_testsuites.split(",")
        run_all = False
        print "Suites going to execute are : "
        print "I AM AT THIS LINE"
        print suite
        app = wx.App(0)
        frame = MDIFrame()
        frame.startRunning()
        exit()










