import React, { useEffect } from "react";
import PropTypes from "prop-types";

import "../page.css";
import Header from "../../base/header/Header";
import Footer from "../../base/footer/Footer";
import SideCard from "../../base/sideCard/SideCard";
import { Button } from "../../base/button/Button";
import Table from "../../composite/table/Table";

export default function OrgVerification({
  page: {
    pageLayout: { header, sideCard1, sideCard2 },
    org
  }
}) {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  const orgVerification = () => {
    alert("You verified the org!");
  };

  const button = {
    label: "Verify",
    buttonStyle: "btn btn-primary",
    buttonSize: "btn btn-sm",
    type: "submit"
  };

  const tableElements = [
    { name: "Org name", value: org.orgNm },
    {
      name: "Org Address",
      value: `${org.addressLine1}\n${org.cityNm}\n${org.provinceNm}\n${org.postalCodeTxt}\n${org.countryNm}`
    },
    { name: "Applicant Relationship", value: "Employee" }
  ];

  const table = {
    header: "Org Information",
    tableElements
  };

  return (
    <main>
      <Header header={header} />
      <div className="page">
        <div className="content col-md-8">
          <Table table={table} />
          <Button button={button} onClick={orgVerification} />
        </div>
        <div className="sidecard">
          <SideCard sideCard={sideCard1} />
          <SideCard sideCard={sideCard2} />
        </div>
      </div>
      <Footer />
    </main>
  );
}

OrgVerification.propTypes = {
  page: PropTypes.shape({
    org: PropTypes.object.isRequired,
    pageLayout: PropTypes.shape({
      header: PropTypes.shape({
        name: PropTypes.string.isRequired
      }),
      sideCard1: PropTypes.shape({
        heading: PropTypes.string.isRequired,
        content: PropTypes.string.isRequired
      }),
      sideCard2: PropTypes.shape({
        heading: PropTypes.string.isRequired,
        content: PropTypes.string.isRequired
      })
    }).isRequired
  }).isRequired
};
