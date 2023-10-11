import {NavLink, useParams, useSearchParams} from "react-router-dom";
import {
  CButton,
  CCard,
  CCardBody,
  CCardHeader,
  CCol,
  CForm,
  CFormLabel,
  CModal,
  CModalFooter,
  CModalHeader,
  CModalTitle,
  CNav,
  CNavItem,
  CNavLink,
  CRow,
  CTabContent,
  CTable,
  CTableBody,
  CTableDataCell,
  CTableHead,
  CTableHeaderCell,
  CTableRow, CTabPane
} from "@coreui/react";
import React, {useEffect, useState} from "react";
import JSONInput from 'react-json-editor-ajrm';
import locale    from 'react-json-editor-ajrm/locale/en';
import CIcon from "@coreui/icons-react";
import {cilCode, cilMediaPlay} from "@coreui/icons";



export default function collection(){
  const params = useParams();
  const [documents,setDocuments]=useState([])
  const [searchParams, setSearchParams] = useSearchParams();
  const [visible, setVisible] = useState(false)
  const [display, setDisplay] = useState(false)
  const [document,setDocument] = useState()
  const [active,setActive]=useState(1)
  const [schema,setSchema]=useState()
  const [documentToUpdate,setDocumentToUpdate]=useState({})
  const color={
    'default':"#FFFFFF",
    'background':"#4f5d73"
  }
  const getDocuments= async (id)=>{
    const documentsJson=await fetch(localStorage.getItem("url")+"/document?databaseName="+searchParams.get("databaseName")+"&collectionName="+params.id, {method: "GET",})
    const documentsData=await documentsJson.json()
    setDocuments(documentsData)
    const schemaJson=await fetch(localStorage.getItem("url")+"/collection/schema?databaseName="+searchParams.get("databaseName")+"&collectionName="+params.id, {method: "GET",})
    const schemaData=await schemaJson.json()
    setSchema(schemaData)
  }
  const submit= async ()=>{
    const documentsJson=await fetch(localStorage.getItem("url")+"/document?databaseName="+searchParams.get("databaseName")+"&collectionName="+params.id, {method: "POST",body:JSON.stringify(document)})
    console.log(documentsJson)
    if(documentsJson.status===200){
      setVisible(false)
      location.reload()
    }
  }
  const update= async ()=>{

    const documentsJson=await fetch(localStorage.getItem("url")+"/document/update?databaseName="+searchParams.get("databaseName")+"&collectionName="+params.id+"&documentName="+documentToUpdate.id, {method: "POST",body:JSON.stringify(documentToUpdate)})
    if(documentsJson.status===200){
      setVisible(false)
      location.reload()
    }
  }
  const deleteCollection= async (id) =>{
    const deleteJson=await fetch(localStorage.getItem("url")+"/collection/delete?databaseName="+searchParams.get("databaseName")+"&collectionName="+params.id, {method: "GET"})

    if(deleteJson.status===200)window.location.href="/#/database/"+searchParams.get("databaseName")
  }
  const deleteDocument= async (id) =>{
    const deleteJson=await fetch(localStorage.getItem("url")+"/document/delete?databaseName="+searchParams.get("databaseName")+"&collectionName="+params.id+"&documentName="+id, {method: "GET"})

    if(deleteJson.status===200)location.reload()
  }
  const handleClick=()=>{
    console.log("nn")
  }
  useEffect(() => {
      getDocuments(params.id)
  }, [params]);
  return(
    <CCol xs={12}>
        <CModal visible={visible} onClose={() => setVisible(false)}>
          <CModalHeader>
            <CModalTitle>Create Document</CModalTitle>
          </CModalHeader>
          <CRow>
            <CCol xs={12}>
              <CCard className="mb-4">
                <CCardBody>
                  <CForm>
                    <div className="mb-3">
                      <CFormLabel htmlFor="a_unique_id">Document</CFormLabel>
                      <JSONInput
                        id          = 'a_unique_id'
                        locale      = { locale }
                        colors ={color}
                        height      = '200px'
                        width = '465px'
                        onChange = {(e)=>setDocument(e.jsObject)}
                      />
                    </div>
                  </CForm>
                </CCardBody>
              </CCard>
            </CCol>
          </CRow>
          <CModalFooter>
            <CButton color="secondary" onClick={() => setVisible(false)}>
              Closee
            </CButton>
            <CButton color="primary" onClick={()=>submit()}>Create</CButton>
          </CModalFooter>
        </CModal>
      <CModal visible={display} onClose={() => setVisible(false)}>
        <CModalHeader>
          <CModalTitle>Create Document</CModalTitle>
        </CModalHeader>
        <CRow>
          <CCol xs={12}>
            <CCard className="mb-4">
              <CCardBody>
                <CForm>
                  <div className="mb-3">
                    <CFormLabel htmlFor="a_unique_id">Document</CFormLabel>
                    <JSONInput
                      id          = 'a_unique_id'
                      locale      = { locale }
                      colors ={color}
                      height      = '200px'
                      width = '465px'
                      placeholder = { documentToUpdate }
                      waitAfterKeyPress={2000}
                      onChange = {(e)=>{
                        console.log(JSON.stringify(documentToUpdate))
                        setDocumentToUpdate(e.jsObject)
                      }}
                    />
                  </div>
                </CForm>
              </CCardBody>
            </CCard>
          </CCol>
        </CRow>
        <CModalFooter>
          <CButton color="secondary" onClick={() => {
            setDisplay(false)

          }}>
            Close
          </CButton>
          <CButton color="primary" onClick={()=>update()}>Update</CButton>
        </CModalFooter>
      </CModal>

      <CCard className="mb-4">
        <CButton
          color={"danger"}
          active={true}
          disabled={false}
          onClick={()=>deleteCollection(params.id)}
        >
          Delete Collection
        </CButton>
        <CButton
          color={"primary"}
          active={true}
          disabled={false}
          onClick={()=>setVisible(true)}
        >
          Add Document
        </CButton>
        <CCardHeader>
          <strong>Documents</strong>
        </CCardHeader>
        <CCardBody>
          <div className="example">
            <CNav variant="tabs">
              <CNavItem>
                <CNavLink onClick={()=>setActive(1)} active={active===1} >
                  <CIcon icon={cilMediaPlay} className="me-2" />
                  Documents
                </CNavLink>
              </CNavItem>
              <CNavItem>
                <CNavLink onClick={()=>setActive(2)} active={active===2}>
                  <CIcon icon={cilCode} className="me-2" />
                  Schema
                </CNavLink>
              </CNavItem>
            </CNav>
            <CTabContent className="rounded-bottom">
              <CTabPane className="p-3 preview" visible>
                {active===2 && <CTable striped>
                  <CTableHead>
                    <CTableRow>
                      <CTableHeaderCell scope="col">Schema</CTableHeaderCell>
                    </CTableRow>
                  </CTableHead>
                  <CTableBody>
                    <JSONInput
                      id          = 'a_unique_id'
                      locale      = { locale }
                      colors ={color}
                      placeholder = {schema?schema:{}}
                      confirmGood={false}
                      viewOnly={true}
                      height      = '200px'
                    />
                  </CTableBody>
                </CTable>}

                {active===1 && <CTable striped>
                  <CTableHead>
                    <CTableRow>
                      <CTableHeaderCell scope="col">#</CTableHeaderCell>
                      <CTableHeaderCell scope="col">Document</CTableHeaderCell>
                      <CTableHeaderCell scope="col">UPDATE</CTableHeaderCell>
                      <CTableHeaderCell scope="col">DELETE</CTableHeaderCell>
                    </CTableRow>
                  </CTableHead>
                  <CTableBody>
                    {documents.map(function(document, i){
                      return (
                        <CTableRow>
                          <CTableHeaderCell scope="row">{i+1}</CTableHeaderCell>
                          <CTableDataCell>
                            <JSONInput
                              id          = 'a_unique_id'
                              placeholder = { document }
                              locale      = { locale }
                              colors ={color}
                              confirmGood={false}
                              viewOnly={true}
                              height      = '200px'
                            />
                          </CTableDataCell>
                          <CTableDataCell>
                            <CButton
                              color={"primary"}
                              active={true}
                              disabled={false}
                              onClick={()=>{
                                setDocumentToUpdate(document)
                                setDisplay(true)

                              }}
                            >
                              Update
                            </CButton>
                          </CTableDataCell>
                          <CTableDataCell>
                            <CButton
                              color={"danger"}
                              active={true}
                              disabled={false}
                              onClick={()=>deleteDocument(document.id)}
                            >
                              Delete
                            </CButton>
                          </CTableDataCell>

                        </CTableRow>)
                    })}
                  </CTableBody>
                </CTable>}
                </CTabPane>
            </CTabContent>
          </div>



        </CCardBody>
      </CCard>
    </CCol>
  )
}
