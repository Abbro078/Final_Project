import React, {useEffect, useState} from "react";
import {
    CButton,
    CCard, CCardBody,
    CCol, CForm, CFormInput, CFormLabel,
    CModal,
    CModalFooter,
    CModalHeader,
    CModalTitle, CRow
} from "@coreui/react";
import CIcon from "@coreui/icons-react";
import {cibAddthis} from "@coreui/icons";


export default function  DatabaseNav () {
    const [visible, setVisible] = useState(false)
    const [databaseName,setDatabaseName]=useState("")
    const submit=()=>{
        if(databaseName!=="") fetch(localStorage.getItem("url")+"/database?databaseName="+databaseName, {method: "POST",})
        location.reload()
    }

  useEffect(() => {
    console.log("database nav")
  }, []);
    return (
        <>
            <CModal visible={visible} onClose={() => setVisible(false)}>
                <CModalHeader>
                    <CModalTitle>Create Database </CModalTitle>
                </CModalHeader>
                <CRow>
                    <CCol xs={12}>
                        <CCard className="mb-4">
                            <CCardBody>
                                    <CForm>
                                        <div className="mb-3">
                                            <CFormLabel htmlFor="exampleFormControlInput1">Database Name</CFormLabel>
                                            <CFormInput
                                                type="email"
                                                id="exampleFormControlInput1"
                                                placeholder="name@example.com"
                                                onChange={(e)=>setDatabaseName(e.target.value)}
                                            />
                                        </div>
                                    </CForm>
                            </CCardBody>
                        </CCard>
                    </CCol>
                </CRow>
                    <CModalFooter>
                    <CButton color="secondary" onClick={() => setVisible(false)}>
                        Close
                    </CButton>
                    <CButton color="primary" onClick={()=>submit()}>Save changes</CButton>
                </CModalFooter>
            </CModal>
            <div className={"d-flex justify-content-between align-items-center"} onClick={() => setVisible(!visible)} >
                <li className={'nav-title mb-3'} >DATABASES</li>
                <CIcon icon={cibAddthis}  className={'mt-3'} customClassName=" nav-icon " />
            </div>
        </>
    )
}
