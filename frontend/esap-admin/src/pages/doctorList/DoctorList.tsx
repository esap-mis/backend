import React, {useEffect, useState} from "react";
import HttpService from "../../service/HttpService";
import {DataGrid, GridColDef, ruRU} from "@mui/x-data-grid";
import {Link} from "react-router-dom";
import {DeleteOutline} from "@mui/icons-material";
import {Doctor} from "../../model/Doctor";
import ScheduleModal from "../../components/scheduleModal/ScheduleModal";
import './doctorList.scss';
import {IconButton} from "@mui/material";
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import EditIcon from '@mui/icons-material/Edit';

const DoctorList: React.FC = () => {
  const [data, setData] = useState<Doctor[]>([]);
  const [open, setOpen] = useState(false);
  const [selectedDoctor, setSelectedDoctor] = useState<Doctor>();

  useEffect(() => {
    HttpService.getDoctorList()
      .then(response => setData(response))
      .catch(error => console.error(error));
  }, []);

  const handleOpen = (doctor: Doctor) => {
    setSelectedDoctor(doctor);
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleDelete = (id: number) => {
    setData(data.filter(item => item.id !== id));
  };

  const columns: GridColDef[] = [
    {
      field: 'id',
      headerName: 'ID',
      width: 50,
    },
    {
      field: 'firstName',
      headerName: 'Имя',
      width: 100,
      renderCell: (params) => {
        return (
          <div className='doctorListField'>{params.row.firstName}</div>
        );
      },
    },
    {
      field: 'patronymic',
      headerName: 'Отчество',
      width: 140,
    },
    {
      field: 'lastName',
      headerName: 'Фамилия',
      width: 120,
    },
    {
      field: 'gender',
      headerName: 'Пол',
      width: 80,
      renderCell: (params) => {
        return (
          <div className='doctorListField'>
            {params.row.gender === 1 ? 'мужской' : 'женский'}
          </div>
        );
      },
    },
    {
      field: 'specialization',
      headerName: 'Специализация',
      width: 150,
    },
    {
      field: 'action',
      headerName: 'Действие',
      width: 270,
      renderCell: (params) => {
        return (
          <>
            <Link to={`/doctor/${params.row.id}`}>
              <IconButton color="primary" aria-label="edit doctor" component="label">
                <EditIcon />
              </IconButton>
            </Link>
            <Link to='#'>
              <IconButton color="primary" aria-label="doctor schedule" component="label" onClick={() => handleOpen(params.row)}>
                <CalendarMonthIcon />
              </IconButton>
            </Link>
            <IconButton color="primary" aria-label="delete doctor" component="label">
              <DeleteOutline
                className='deleteButton'
                onClick={() => handleDelete(params.row.id)}
              />
            </IconButton>
          </>
        );
      }
    }
  ];

  return (
    <div className='doctorListPage'>
      <DataGrid
        localeText={ruRU.components.MuiDataGrid.defaultProps.localeText}
        rows={data}
        disableSelectionOnClick
        columns={columns}
        pageSize={13}
        rowsPerPageOptions={[13]}
        checkboxSelection
        sx={{border: "none"}}
      />
      {selectedDoctor && (
        <ScheduleModal
          doctor={selectedDoctor}
          schedules={selectedDoctor.schedules}
          open={open}
          onClose={handleClose}
        />
      )}
    </div>
  );
};

export default DoctorList;