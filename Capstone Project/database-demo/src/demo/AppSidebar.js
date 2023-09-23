import React, {useEffect, useState} from 'react'
import {useSelector, useDispatch, useStore} from 'react-redux'

import { CSidebar, CSidebarBrand, CSidebarNav, CSidebarToggler } from '@coreui/react'
import CIcon from '@coreui/icons-react'

import { AppSidebarNav } from './AppSidebarNav'

import { logoNegative } from 'src/assets/brand/logo-negative'
import { sygnet } from 'src/assets/brand/sygnet'

import SimpleBar from 'simplebar-react'
import 'simplebar/dist/simplebar.min.css'

// sidebar nav config
import navigation from '../_nav'
import { CNavGroup, CNavItem, CNavTitle } from '@coreui/react'
import {cilBook, cilDrop, cilSpeedometer} from '@coreui/icons'
import {array} from "prop-types";
const AppSidebar = () => {
  const dispatch = useDispatch()
  const unfoldable = useSelector((state) => state.sidebarUnfoldable)
  const sidebarShow = useSelector((state) => state.sidebarShow)
  const [navItems,setNavItems]=useState([
    {
      component: CNavItem,
      name: 'Dashboard',
      to: '/dashboard',
      icon: <CIcon icon={cilSpeedometer} customClassName="nav-icon" />,
    },
    {
      component: CNavTitle,
      name: 'Admin',
    },
    {
      component: CNavItem,
      name: 'Users',
      to: '/users',
      icon: <CIcon icon={cilDrop} customClassName="nav-icon" />,
    },
    {
      component: CNavTitle,
      name: 'Databases',
      database:true
    },
  ])

  const getDatabasesAndCollections= async  ()=>{
    const databasesJson=await fetch(localStorage.getItem("url")+"/database", {method: "GET",})
    const databases=await databasesJson.json()
    let groups=[]
    for(let i=0;i<databases.length;i++){
      let group={}
      group.component=CNavItem;
      group.name=databases[i]
      group.to="/database/"+databases[i];
      group.icon= <CIcon icon={cilDrop} customClassName="nav-icon" />
      // group.items=[];
      // const collectionsJson=await fetch("http://localhost:8080/collection?databaseName="+databases[i], {method: "GET",})
      // const collections=await collectionsJson.json()
      // for(let j=0;j<collections.length;j++){
      //   let navItem={}
      //   navItem.component=CNavItem;
      //   navItem.name=collections[j]
      //   navItem.to="/collection/"+collections[j];
      //   group.items.push(navItem)
      // }
      groups.push(group)
    }
    setNavItems(prevState => [...prevState,...groups])
  }
  useEffect(() => {
    getDatabasesAndCollections().then(r => "");
  }, []);
  return (
    <CSidebar
      position="fixed"
      unfoldable={unfoldable}
      visible={sidebarShow}
      onVisibleChange={(visible) => {
        dispatch({ type: 'set', sidebarShow: visible })
      }}
    >
      <CSidebarBrand className="d-none d-md-flex" to="/">
      <h5 className={""}>
        Decentralized Database

      </h5>
      </CSidebarBrand>
      <CSidebarNav>
        <SimpleBar>
          <AppSidebarNav items={[...navItems
            // ,...navigation
          ]} />
        </SimpleBar>
      </CSidebarNav>
      <CSidebarToggler
        className="d-none d-lg-flex"
        onClick={() => dispatch({ type: 'set', sidebarUnfoldable: !unfoldable })}
      />
    </CSidebar>
  )
}

export default React.memo(AppSidebar)
