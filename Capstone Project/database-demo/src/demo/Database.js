import {NavLink, useParams} from "react-router-dom";
import {
  CButton,
  CCard,
  CCardBody,
  CCardHeader,
  CCol,
  CForm,
  CFormInput,
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
import JSONInput from "react-json-editor-ajrm";
import locale from "react-json-editor-ajrm/locale/en";
import CIcon from "@coreui/icons-react";
import {cilCode, cilMediaPlay} from "@coreui/icons";


export default function database(){
  const params = useParams();
  const [collections,setCollections]=useState([])
  const [visible,setVisible]=useState(false)
  const [schema,setSchema]=useState()
  const [collectionName,setCollectionName]=useState("")
  const getCollections= async (id)=>{
    const collectionsJson=await fetch(localStorage.getItem("url")+"/collection?databaseName="+id, {method: "GET",})
    const collectionsData=await collectionsJson.json()
    setCollections(collectionsData)
  }
  const color={
    'default':"#FFFFFF",
    'background':"#4f5d73"
  }

  const submit= async ()=>{
    const documentsJson=await fetch(localStorage.getItem("url")+"/collection?databaseName="+params.id+"&collectionName="+collectionName, {method: "POST",body:JSON.stringify(schema)})
    console.log(documentsJson)
    if(documentsJson.status===200){
      setVisible(false)
      location.reload()
    }
  }

  const deleteDatabase= async (id) =>{
    const deleteJson=await fetch(localStorage.getItem("url")+"/database/delete?databaseName="+id, {method: "GET",})
    if(deleteJson.status===200)window.location.href="/"
  }
  useEffect(() => {
    getCollections(params.id)
  }, [params]);
  return(

    <CCol xs={12}>
      <CModal visible={visible} onClose={() => setVisible(false)}>
        <CModalHeader>
          <CModalTitle>Create Collection</CModalTitle>
        </CModalHeader>
        <CRow>
          <CCol xs={12}>
            <CCard className="mb-4">
              <CCardBody>
                <CForm>
                  <div className="mb-3">
                    <CFormLabel htmlFor="collectionName">Collection Name</CFormLabel>
                    <CFormInput
                      type="email"
                      id="collectionName"
                      placeholder="name@example.com"
                      onChange={(e)=>setCollectionName(e.target.value)}
                    />
                    <CFormLabel htmlFor="a_unique_id">Collection Schema</CFormLabel>
                      <JSONInput
                        id          = 'a_unique_id'
                        locale      = { locale }
                        colors ={color}
                        height      = '200px'
                        width = '465px'
                        onChange = {(e)=>setSchema(e.jsObject)}
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
          <CButton color="primary" onClick={()=>submit()}>Save changes</CButton>
        </CModalFooter>
      </CModal>
      <CCard className="mb-4">
        <CButton
          color={"danger"}
          active={true}
          disabled={false}
          onClick={()=>deleteDatabase(params.id)}
        >
          Delete Database
        </CButton>
        <CButton
          color={"primary"}
          active={true}
          disabled={false}
          onClick={()=>setVisible(true)}
        >
          Add Collection
        </CButton>
        <CCardHeader>
          <strong>Collections</strong>
        </CCardHeader>
        <CCardBody>

          <div className="example">
            <CNav variant="tabs">
              <CNavItem>
                <CNavLink href="#" active>
                  <CIcon icon={cilMediaPlay} className="me-2" />
                  Collections
                </CNavLink>
              </CNavItem>

            </CNav>
            <CTabContent className="rounded-bottom">
              <CTabPane className="p-3 preview" visible>
                <CTable striped>
                  <CTableHead>
                    <CTableRow>
                      <CTableHeaderCell scope="col">#</CTableHeaderCell>
                      <CTableHeaderCell scope="col">name</CTableHeaderCell>
                      <CTableHeaderCell scope="col">NO. documents</CTableHeaderCell>
                    </CTableRow>
                  </CTableHead>
                  <CTableBody>
                    {
                      collections.map(function(collection, i){
                        return (<CTableRow>
                          <CTableHeaderCell scope="row">{i+1}</CTableHeaderCell>
                          <CTableDataCell>
                            <NavLink to={"/collection/"+collection.name+"?databaseName="+params.id} >
                              {collection.name}
                            </NavLink>
                          </CTableDataCell>
                          <CTableDataCell>{collection.size}</CTableDataCell>
                        </CTableRow>)
                      })}
                  </CTableBody>
                </CTable>
              </CTabPane>
            </CTabContent>
          </div>




        </CCardBody>
      </CCard>
    </CCol>
  )
}
