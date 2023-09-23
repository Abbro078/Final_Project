import { useParams} from "react-router-dom";
import {
  CCard,
  CCardBody,
  CCardHeader,
  CCol,
  CNav,
  CNavItem,
  CNavLink,
  CTabContent,
  CTable,
  CTableBody,
  CTableDataCell,
  CTableHead,
  CTableHeaderCell,
  CTableRow, CTabPane
} from "@coreui/react";
import React, {useEffect, useState} from "react";
import CIcon from "@coreui/icons-react";
import { cilMediaPlay} from "@coreui/icons";


export default function database(){
  const params = useParams();
  const [collections,setCollections]=useState([])
  const getCollections= async (id)=>{
      const loginJson=await fetch("http://localhost:8000/database/users",{method:"GET"})
      const loginData=await loginJson.json()
      setCollections(loginData)
  }


  useEffect(() => {
    getCollections(params.id)
  }, [params]);
  return(

    <CCol xs={12}>
      <CCard className="mb-4">
        <CCardHeader>
          <strong>Users</strong>
        </CCardHeader>
        <CCardBody>

          <div className="example">
            <CNav variant="tabs">
              <CNavItem>
                <CNavLink href="#" active>
                  <CIcon icon={cilMediaPlay} className="me-2" />
                    Users
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
                    </CTableRow>
                  </CTableHead>
                  <CTableBody>
                    {
                      collections.map(function(collection, i){
                        return (<CTableRow>
                          <CTableHeaderCell scope="row">{i+1}</CTableHeaderCell>
                          <CTableDataCell>
                              {collection}
                          </CTableDataCell>
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
