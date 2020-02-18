import React from "react";

import OrgVerification from "./OrgVerification";

export default {
  title: "OrgVerification",
  component: OrgVerification
};

const header = {
  name: "Criminal Record Check"
};

const sideCard1 = {
  heading: "Contact Your Organization",
  content:
    "You require a valid Organization Code, supplied by the organization you are applying to. You must contact them in order to receive this code.",
  type: "blue"
};

const sideCard2 = {
  heading: "Get a BC Services Card",
  content:
    "B.C. residents who have lived in the province for at least six months must use a BC Services Card to log in to the online qualification tool. Learn how to get a card.",
  type: "blue"
};

const pageLayout = {
  header,
  sideCard1,
  sideCard2
};

const org = {
  orgNm: "Test Org Name",
  addressLine1: "123 Somewhere Lane",
  cityNm: "Nowhere",
  provinceNm: "British Columbia",
  countryNm: "Canada",
  contactPhoneNo: "250 123 4567",
  orgApplicantRelationship: "Employee"
};

const setOrg = () => {};

const page = {
  org,
  setOrg,
  pageLayout
};

export const Default = () => <OrgVerification page={page} />;

export const Mobile = () => <OrgVerification page={page} />;

Mobile.story = {
  parameters: {
    viewport: {
      defaultViewport: "mobile2"
    }
  }
};
