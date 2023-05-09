import {Routes, Route, Navigate} from 'react-router-dom';
import Home from './pages/home/Home';
import NewPatient from './pages/newPatient/NewPatient';
import PatientList from './pages/patientList/PatientList';
import 'bootstrap/dist/css/bootstrap.min.css';
import MainLayout from "./layout/MainLayout";
import LoginForm from "./components/auth/LoginForm";
import React, {useState} from "react";
import RegistrationPage from "./pages/registration/Registration";
import Queue from "./components/queue/Queue";
import DoctorList from "./pages/doctorList/DoctorList";
import MedicalCard from './pages/medicalCard/PatientMedicalCard';
import {TokenStorageService} from './service/auth/TokenStorageService';
import ResetPasswordForm from "./components/auth/ResetPasswordForm";
import EditingMedicalRecord from './pages/editingMedicalRecord/EditingMedicalRecord';
import Patient from "./pages/patient/Patient";

const App: React.FC = () => {
  const [tokenStorageService] = useState<TokenStorageService>(new TokenStorageService());
  const [isAuthenticated] = useState<Boolean>(Boolean(tokenStorageService.getToken()));

  return (
    <Routes>
      {isAuthenticated ? (
        <Route path="/" element={<MainLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/doctors" element={<DoctorList />} />
          <Route path="/doctor/:doctorId" element={<Patient />} />
          <Route path="/patients" element={<PatientList />} />
          <Route path="/patient/:patientId" element={<Patient />} />
          <Route path="/medicalCard/:patientId" element={<MedicalCard />} />
          <Route path="/editingMedicalCard/:patientId" element={<EditingMedicalRecord />} />
          <Route path="/newPatient" element={<NewPatient />} />
          <Route path="/queue/:scheduleId" element={<Queue />} />
        </Route>
      ) : (
        <Route path="*" element={<Navigate to="/" />} />
      )}
      <Route path="/" element={<LoginForm />} />
      <Route path="/register" element={<RegistrationPage />} />
      <Route path="/password/reset" element={<ResetPasswordForm />} />
    </Routes>
  );
};

export default App;
