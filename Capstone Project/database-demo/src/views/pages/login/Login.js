import React, {useState} from 'react'
import { Link } from 'react-router-dom'
import {
  CButton,
  CCard,
  CCardBody,
  CCardGroup,
  CCol,
  CContainer,
  CForm,
  CFormInput,
  CInputGroup,
  CInputGroupText,
  CRow,
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import { cilLockLocked, cilUser } from '@coreui/icons'


const Login = () => {
  const [password,setPassword]=useState("")
  const [username,setUsername]=useState("")
  const [databaseUrl,setDatabaseUrl]=useState("")
  const [error,setError]=useState()
  const handleSubmit= async ()=>{
    if( password!=="" && username!=="" && databaseUrl!==""){
      const loginJson=await fetch("http://localhost:8000/database/login?username="+username+"&password="+password,{method:"POST"})
      const loginData=await loginJson.json()
      if(loginData.value===true){
        localStorage.setItem("loggedIn",true);
        localStorage.setItem("url",databaseUrl)
        window.location.href="/#/dashboard"
      }
      else{
        setError("wrong credentials")
      }
    }
  }
  return (
    <div className="bg-light min-vh-100 d-flex flex-row align-items-center">
      <CContainer>
        <CRow className="justify-content-center">
          <CCol md={8}>
            <CCardGroup>
              <CCard className="p-4">
                <CCardBody>
                  <CForm>
                    <h1>Login</h1>
                    <p className="text-medium-emphasis">Sign In to your account</p>
                    <CInputGroup className="mb-3">
                      <CInputGroupText>
                        <CIcon icon={cilUser} />
                      </CInputGroupText>
                      <CFormInput placeholder="Username" autoComplete="username"
                        onChange={(e)=>setUsername(e.target.value)}
                      />
                    </CInputGroup>
                    <CInputGroup className="mb-4">
                      <CInputGroupText>
                        <CIcon icon={cilLockLocked} />
                      </CInputGroupText>
                      <CFormInput
                        type="password"
                        placeholder="Password"
                        autoComplete="current-password"
                        onChange={(e)=>setPassword(e.target.value)}
                      />
                    </CInputGroup>
                    {error && <p  style={{color: 'red'}}>{error}</p>}
                    <CInputGroup className="mb-3">
                      <CInputGroupText>
                        <CIcon icon={cilUser} />
                      </CInputGroupText>
                      <CFormInput placeholder="Database Url" autoComplete="Database Url"
                                  onChange={(e)=>setDatabaseUrl(e.target.value)}
                      />
                    </CInputGroup>
                    <CRow>
                      <CCol xs={6}>
                        <CButton color="primary" className="px-4" onClick={()=>handleSubmit()}>
                          Login
                        </CButton>
                      </CCol>
                    </CRow>
                  </CForm>
                </CCardBody>
              </CCard>

            </CCardGroup>
          </CCol>
        </CRow>
      </CContainer>
    </div>
  )
}

export default Login
