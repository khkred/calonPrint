package sg.com.argus.www.conquestgroup.models;
public class Client {
    String UserId;
    String Password;
    String loginType;
    String orgId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String username;
    int apmcId;

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String stateDesc) {
        this.stateDesc = stateDesc;
    }

    public String getStateDescEn() {
        return stateDescEn;
    }

    public void setStateDescEn(String stateDescEn) {
        this.stateDescEn = stateDescEn;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getGmStateId() {
        return gmStateId;
    }

    public void setGmStateId(String gmStateId) {
        this.gmStateId = gmStateId;
    }

    public String getListStates() {
        return listStates;
    }

    public void setListStates(String listStates) {
        this.listStates = listStates;
    }

    String statusMsg,stateId,stateDesc,stateDescEn,stateCode,gmStateId,listStates;


    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }


    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getApmcId() {
        return apmcId;
    }

    public void setApmcId(int apmcId) {
        this.apmcId = apmcId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
